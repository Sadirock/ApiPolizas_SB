package com.segurosbolivar.polizasapp;

import com.segurosbolivar.polizasapp.config.ApiKeyFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 
 * @Transactional o sea hace rollback al final de cada test, así cada uno parte
 *                del mismo estado inicial sin necesidad de limpiar nada a mano.
 */

@SpringBootTest
@Transactional
class PolizasApiIntegrationTests {

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private ApiKeyFilter apiKeyFilter;

        private MockMvc mockMvc;

        private static final String API_KEY = "123456";
        private static final String HEADER = "x-api-key";

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .addFilters(apiKeyFilter)
                                .build();
        }

        // SEGURIDAD
        @Test
        void sinApiKey_retorna401() throws Exception {
                mockMvc.perform(get("/polizas"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void apiKeyIncorrecta_retorna401() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, "wrongkey"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void apiKeyCorrecta_permiteAcceso() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY))
                                .andExpect(status().isOk());
        }

        // GET /polizas
        @Test
        void listarPolizas_sinFiltros_retornaLas4() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(4)));
        }

        @Test
        void listarPolizas_filtroPorTipoColectiva_retornaSoloColectivas() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY).param("tipo", "COLECTIVA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[*].tipo", everyItem(is("COLECTIVA"))));
        }

        @Test
        void listarPolizas_filtroPorEstadoActiva_retornaSoloActivas() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY).param("estado", "ACTIVA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[*].estado", everyItem(is("ACTIVA"))));
        }

        @Test
        void listarPolizas_filtroCombinado_retornaInterseccion() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY)
                                .param("tipo", "INDIVIDUAL").param("estado", "ACTIVA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].tipo", is("INDIVIDUAL")))
                                .andExpect(jsonPath("$[0].estado", is("ACTIVA")));
        }

        @Test
        void listarPolizas_estadoInvalido_retorna400ConDetalle() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY).param("estado", "ACTIVA2"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)))
                                .andExpect(jsonPath("$.error", containsString("estado")));
        }

        @Test
        void listarPolizas_tipoInvalido_retorna400() throws Exception {
                mockMvc.perform(get("/polizas").header(HEADER, API_KEY).param("tipo", "INEXISTENTE"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)));
        }

        // GET /polizas/{id}/riesgos
        @Test
        void listarRiesgos_polizaExistente_retornaRiesgos() throws Exception {
                mockMvc.perform(get("/polizas/2/riesgos").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void listarRiesgos_polizaConUnRiesgo_retornaListaDeUno() throws Exception {
                mockMvc.perform(get("/polizas/1/riesgos").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void listarRiesgos_polizaNoExistente_retorna404() throws Exception {
                mockMvc.perform(get("/polizas/999/riesgos").header(HEADER, API_KEY))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status", is(404)));
        }

        // POST /polizas/{id}/renovar
        @Test
        void renovar_polizaActiva_aplicaIpcYCambiaEstado() throws Exception {
                // canon 150000 * 1.05 = 157500, prima 12000 * 1.05 = 12600
                mockMvc.perform(post("/polizas/1/renovar").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado", is("RENOVADA")))
                                .andExpect(jsonPath("$.canon").value(157500.00))
                                .andExpect(jsonPath("$.prima").value(12600.00));
        }

        @Test
        void renovar_polizaRenovada_aplicaIpcNuevamente() throws Exception {
                // Póliza 3 ya es RENOVADA, se puede volver a renovar
                mockMvc.perform(post("/polizas/3/renovar").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado", is("RENOVADA")));
        }

        @Test
        void renovar_polizaCancelada_retorna422() throws Exception {
                mockMvc.perform(post("/polizas/4/renovar").header(HEADER, API_KEY))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.status", is(422)));
        }

        @Test
        void renovar_polizaNoExistente_retorna404() throws Exception {
                mockMvc.perform(post("/polizas/999/renovar").header(HEADER, API_KEY))
                                .andExpect(status().isNotFound());
        }

        // POST /polizas/{id}/cancelar
        @Test
        void cancelar_polizaActiva_quedaCanceladaYRiesgosTambien() throws Exception {
                mockMvc.perform(post("/polizas/2/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado", is("CANCELADA")));

                // verificar que los riesgos asociados también quedaron cancelados
                mockMvc.perform(get("/polizas/2/riesgos").header(HEADER, API_KEY))
                                .andExpect(jsonPath("$[*].estado", everyItem(is("CANCELADO"))));
        }

        @Test
        void cancelar_polizaYaCancelada_retorna422() throws Exception {
                mockMvc.perform(post("/polizas/4/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.status", is(422)));
        }

        @Test
        void cancelar_polizaNoExistente_retorna404() throws Exception {
                mockMvc.perform(post("/polizas/999/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().isNotFound());
        }

        // POST /polizas/{id}/riesgos
        @Test
        void agregarRiesgo_polizaColectivaActiva_creaRiesgo() throws Exception {
                String body = """
                                {"descripcion":"Incendio bodega","asegurado":"Pedro Gomez"}
                                """;
                mockMvc.perform(post("/polizas/2/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.descripcion", is("Incendio bodega")))
                                .andExpect(jsonPath("$.asegurado", is("Pedro Gomez")))
                                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                                .andExpect(jsonPath("$.polizaId", is(2)));
        }

        @Test
        void agregarRiesgo_polizaIndividual_retorna422() throws Exception {
                String body = """
                                {"descripcion":"Extra","asegurado":"Maria Lopez"}
                                """;
                mockMvc.perform(post("/polizas/1/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.status", is(422)));
        }

        @Test
        void agregarRiesgo_polizaCancelada_retorna422() throws Exception {
                String body = """
                                {"descripcion":"Nuevo","asegurado":"Empresa X"}
                                """;
                mockMvc.perform(post("/polizas/4/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().is(422));
        }

        @Test
        void agregarRiesgo_sinBody_retorna400() throws Exception {
                mockMvc.perform(post("/polizas/2/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        void agregarRiesgo_descripcionVacia_retornaErrorDeValidacion() throws Exception {
                String body = """
                                {"descripcion":"","asegurado":"Alguien"}
                                """;
                mockMvc.perform(post("/polizas/2/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.fieldErrors.descripcion").exists());
        }

        @Test
        void agregarRiesgo_ambosCamposVacios_retornaAmbosErrores() throws Exception {
                String body = """
                                {"descripcion":"","asegurado":""}
                                """;
                mockMvc.perform(post("/polizas/2/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.fieldErrors.descripcion").exists())
                                .andExpect(jsonPath("$.fieldErrors.asegurado").exists());
        }

        @Test
        void agregarRiesgo_polizaNoExistente_retorna404() throws Exception {
                String body = """
                                {"descripcion":"Riesgo nuevo","asegurado":"Alguien"}
                                """;
                mockMvc.perform(post("/polizas/999/riesgos").header(HEADER, API_KEY)
                                .contentType(MediaType.APPLICATION_JSON).content(body))
                                .andExpect(status().isNotFound());
        }

        // POST /riesgos/{id}/cancelar
        @Test
        void cancelarRiesgo_riesgoActivoDeColectiva_cancelaCorrectamente() throws Exception {
                // Riesgo 2 es de poliza 2 (COLECTIVA) que tiene 2 activos, se puede cancelar
                // uno
                mockMvc.perform(post("/riesgos/2/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado", is("CANCELADO")));
        }

        @Test
        void cancelarRiesgo_unicoActivoDeIndividual_retorna422() throws Exception {
                // Riesgo 1 es el único activo de poliza 1 (INDIVIDUAL), no se puede cancelar
                mockMvc.perform(post("/riesgos/1/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.status", is(422)));
        }

        @Test
        void cancelarRiesgo_riesgoYaCancelado_retorna422() throws Exception {
                // Riesgo 5 ya está CANCELADO
                mockMvc.perform(post("/riesgos/5/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.status", is(422)));
        }

        @Test
        void cancelarRiesgo_riesgoNoExistente_retorna404() throws Exception {
                mockMvc.perform(post("/riesgos/999/cancelar").header(HEADER, API_KEY))
                                .andExpect(status().isNotFound());
        }
}
