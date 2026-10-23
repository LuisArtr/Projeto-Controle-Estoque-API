package com.luisartr.bakery.production;

import com.luisartr.bakery.TestcontainersConfig;
import com.luisartr.bakery.common.ConflictException;
import com.luisartr.bakery.ingredient.IngredientRequest;
import com.luisartr.bakery.ingredient.IngredientService;
import com.luisartr.bakery.product.ProductRequest;
import com.luisartr.bakery.product.ProductService;
import com.luisartr.bakery.product.RecipeItemRequest;
import com.luisartr.bakery.product.RecipeRequest;
import com.luisartr.bakery.stock.StockBatchRepository;
import com.luisartr.bakery.stock.StockEntryRequest;
import com.luisartr.bakery.stock.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfig.class)
class ProductionIntegrationTest {

    @Autowired IngredientService ingredientService;
    @Autowired ProductService productService;
    @Autowired StockService stockService;
    @Autowired ProductionService productionService;
    @Autowired StockBatchRepository batchRepository;

    private static final LocalDate HOJE = LocalDate.now();

    /** Nome único por teste, porque insumo e produto têm nome único no banco. */
    private String nome(String base) {
        return base + " " + UUID.randomUUID().toString().substring(0, 8);
    }

    private BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    private Long novoInsumo(String nome) {
        return ingredientService.create(new IngredientRequest(nome(nome), "kg", bd("1"))).id();
    }

    private Long novoLote(Long insumoId, String qtd, String custo, LocalDate validade) {
        return stockService.registerEntry(
                new StockEntryRequest(insumoId, null, bd(qtd), bd(custo), validade)).id();
    }

    private Long novoPao(Long farinhaId, Long salId) {
        Long produtoId = productService.create(
                new ProductRequest(nome("Pao"), bd("0.80"), "un", bd("700"))).id();
        productService.replaceRecipe(produtoId, new RecipeRequest(List.of(
                new RecipeItemRequest(farinhaId, bd("30")),
                new RecipeItemRequest(salId, bd("0.6")))));
        return produtoId;
    }

    private BigDecimal saldoDoLote(Long loteId) {
        return batchRepository.findById(loteId).orElseThrow().getCurrentQuantity();
    }

    @Test
    void producaoConsomeLotesPorFefoECalculaOCusto() {
        Long farinha = novoInsumo("Farinha");
        Long sal = novoInsumo("Sal");
        Long loteQueVenceAntes = novoLote(farinha, "20", "4.00", HOJE.plusDays(10));
        Long loteQueVenceDepois = novoLote(farinha, "500", "4.20", HOJE.plusDays(60));
        novoLote(sal, "10", "2.50", HOJE.plusDays(60));
        Long pao = novoPao(farinha, sal);

        ProductionResponse resultado = productionService.produce(new ProductionRequest(pao, bd("1")));

        // o lote que vence antes é esvaziado primeiro, e o resto sai do outro
        assertThat(saldoDoLote(loteQueVenceAntes)).isEqualByComparingTo("0");
        assertThat(saldoDoLote(loteQueVenceDepois)).isEqualByComparingTo("490");
        assertThat(stockService.balance(farinha).balance()).isEqualByComparingTo("490");

        // 20 x 4,00 + 10 x 4,20 + 0,6 x 2,50 = 123,50
        assertThat(resultado.quantityProduced()).isEqualByComparingTo("700");
        assertThat(resultado.totalCost()).isEqualByComparingTo("123.50");
        assertThat(resultado.consumption()).hasSize(3);
    }

    @Test
    void estoqueInsuficienteNaoBaixaNada() {
        Long farinha = novoInsumo("Farinha");
        Long sal = novoInsumo("Sal");
        Long loteFarinha = novoLote(farinha, "10", "4.00", HOJE.plusDays(10));
        Long loteSal = novoLote(sal, "10", "2.50", HOJE.plusDays(60));
        Long pao = novoPao(farinha, sal);

        assertThatThrownBy(() -> productionService.produce(new ProductionRequest(pao, bd("1"))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("faltam 20");

        // a transação desfez tudo: nem o sal, que tinha saldo, foi baixado
        assertThat(saldoDoLote(loteFarinha)).isEqualByComparingTo("10");
        assertThat(saldoDoLote(loteSal)).isEqualByComparingTo("10");
    }

    @Test
    void loteVencidoNaoEhUsadoNaProducao() {
        Long farinha = novoInsumo("Farinha");
        Long sal = novoInsumo("Sal");
        Long loteVencido = novoLote(farinha, "100", "3.00", HOJE.minusDays(1));
        Long loteValido = novoLote(farinha, "40", "4.00", HOJE.plusDays(20));
        novoLote(sal, "10", "2.50", HOJE.plusDays(60));
        Long pao = novoPao(farinha, sal);

        productionService.produce(new ProductionRequest(pao, bd("1")));

        assertThat(saldoDoLote(loteVencido)).isEqualByComparingTo("100");
        assertThat(saldoDoLote(loteValido)).isEqualByComparingTo("10");
    }
}