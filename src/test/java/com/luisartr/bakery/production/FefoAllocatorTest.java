package com.luisartr.bakery.production;

import com.luisartr.bakery.stock.StockBatch;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FefoAllocatorTest {

    private static final LocalDate DEZ = LocalDate.of(2026, 12, 1);
    private static final LocalDate JAN = LocalDate.of(2027, 1, 15);
    private static final LocalDate MAR = LocalDate.of(2027, 3, 1);

    /** Cria um lote em memória, com id definido por reflexão (a entidade não tem setter). */
    private StockBatch batch(Long id, String quantity, LocalDate expiration) {
        StockBatch b = new StockBatch(1L, null, new BigDecimal(quantity),
                new BigDecimal("4.00"), expiration);
        ReflectionTestUtils.setField(b, "id", id);
        return b;
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    @Test
    void umLoteBasta() {
        var lote = batch(1L, "50", MAR);

        var result = FefoAllocator.allocate(List.of(lote), bd("30"));

        assertThat(result.allocations()).hasSize(1);
        assertThat(result.allocations().get(0).batch()).isSameAs(lote);
        assertThat(result.allocations().get(0).quantity()).isEqualByComparingTo("30");
        assertThat(result.shortfall()).isEqualByComparingTo("0");
    }

    @Test
    void consomeOLoteQueVenceAntesEDepoisOProximo() {
        var dezembro = batch(1L, "20", DEZ);
        var marco = batch(2L, "500", MAR);

        var result = FefoAllocator.allocate(List.of(dezembro, marco), bd("30"));

        assertThat(result.allocations()).hasSize(2);
        assertThat(result.allocations().get(0).batch()).isSameAs(dezembro);
        assertThat(result.allocations().get(0).quantity()).isEqualByComparingTo("20");
        assertThat(result.allocations().get(1).batch()).isSameAs(marco);
        assertThat(result.allocations().get(1).quantity()).isEqualByComparingTo("10");
        assertThat(result.shortfall()).isEqualByComparingTo("0");
    }

    @Test
    void ordemDaListaNaoImporta() {
        var dezembro = batch(1L, "20", DEZ);
        var janeiro = batch(2L, "20", JAN);
        var marco = batch(3L, "20", MAR);

        // lista propositalmente fora de ordem
        var result = FefoAllocator.allocate(List.of(marco, dezembro, janeiro), bd("45"));

        assertThat(result.allocations()).extracting(a -> a.batch().getId())
                .containsExactly(1L, 2L, 3L);
        assertThat(result.allocations()).extracting(a -> a.quantity().intValue())
                .containsExactly(20, 20, 5);
    }

    @Test
    void estoqueInsuficienteInformaOQueFalta() {
        var lote = batch(1L, "20", DEZ);

        var result = FefoAllocator.allocate(List.of(lote), bd("30"));

        assertThat(result.allocations()).hasSize(1);
        assertThat(result.allocations().get(0).quantity()).isEqualByComparingTo("20");
        assertThat(result.shortfall()).isEqualByComparingTo("10");
    }

    @Test
    void semLotesTudoFalta() {
        var result = FefoAllocator.allocate(List.of(), bd("30"));

        assertThat(result.allocations()).isEmpty();
        assertThat(result.shortfall()).isEqualByComparingTo("30");
    }

    @Test
    void quantidadeExataEsvaziaOLoteSemSobrarFalta() {
        var lote = batch(1L, "30", DEZ);

        var result = FefoAllocator.allocate(List.of(lote), bd("30"));

        assertThat(result.allocations()).hasSize(1);
        assertThat(result.shortfall()).isEqualByComparingTo("0");
    }

    @Test
    void loteVazioEIgnorado() {
        var vazio = batch(1L, "0", DEZ);
        var cheio = batch(2L, "50", MAR);

        var result = FefoAllocator.allocate(List.of(vazio, cheio), bd("10"));

        assertThat(result.allocations()).hasSize(1);
        assertThat(result.allocations().get(0).batch()).isSameAs(cheio);
    }

    @Test
    void mesmaValidadeDesempataPeloMenorId() {
        var segundo = batch(2L, "10", DEZ);
        var primeiro = batch(1L, "10", DEZ);

        var result = FefoAllocator.allocate(List.of(segundo, primeiro), bd("5"));

        assertThat(result.allocations()).hasSize(1);
        assertThat(result.allocations().get(0).batch().getId()).isEqualTo(1L);
    }

    @Test
    void aceitaQuantidadesFracionadas() {
        var lote = batch(1L, "1", DEZ);

        var result = FefoAllocator.allocate(List.of(lote), bd("0.300"));

        assertThat(result.allocations().get(0).quantity()).isEqualByComparingTo("0.3");
        assertThat(result.shortfall()).isEqualByComparingTo("0");
    }
}