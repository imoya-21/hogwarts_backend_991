package org.lapaloma.hogwarts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lapaloma.hogwarts.dao.ICasaDAO;
import org.lapaloma.hogwarts.excepcion.CasaNoEncontradaException;
import org.lapaloma.hogwarts.vo.Casa;

class CasaServiceTest {

    private CasaService casaService;
    private FakeCasaDAO fakeDAO;

    @BeforeEach
    void setUp() {
        fakeDAO = new FakeCasaDAO();
        casaService = new CasaService(fakeDAO);
    }

    // =========================
    // obtenerCasaPorClave
    // =========================

    @Test
    void obtenerCasaPorClave_cuandoCodigoEsNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            casaService.obtenerCasaPorClave(null);
        });
    }

    @Test
    void obtenerCasaPorClave_cuandoCodigoEstaVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            casaService.obtenerCasaPorClave(0);
        });
    }

    @Test
    void obtenerCasaPorClave_cuandoNoExiste_lanzaExcepcion() {
        assertThrows(CasaNoEncontradaException.class, () -> {
            casaService.obtenerCasaPorClave(99);
        });
    }

    @Test
    void obtenerCasaPorClave_cuandoExiste_retornaCasa() {
        fakeDAO.crearCasa(new Casa(2, "Slytherin"));

        Casa resultado = casaService.obtenerCasaPorClave(2);

        assertNotNull(resultado);
        assertEquals("Slytherin", resultado.getNombre());
    }

    // CORRECCIÓN 1: obtenerListaCasas
    @Test
    void obtenerListaCasas_cuandoHayDatos_retornaLista() {
        // 1. Preparamos el dato en el FAKE
        Casa s = new Casa(2, "Slytherin");
        fakeDAO.crearCasa(s);

        // 2. Ejecutamos el servicio
        List<Casa> resultado = casaService.obtenerListaCasas();

        // 3. Verificamos
        assertNotNull(resultado, "La lista no debería ser nula");
        assertEquals(1, resultado.size(), "La lista debería tener exactamente 1 casa");
    }

    // CORRECCIÓN 2: obtenerCasaPorNombre
    @Test
    void obtenerCasaPorNombre_cuandoExiste_retornaLista() {
        // 1. Preparamos el dato
        fakeDAO.crearCasa(new Casa(2, "Slytherin"));

        // 2. Ejecutamos
        List<Casa> resultado = casaService.obtenerCasaPorNombre("Slytherin");

        // 3. Verificamos
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Debería haber encontrado 1 casa llamada Slytherin");
        assertEquals("Slytherin", resultado.get(0).getNombre());
    }

    // =========================
    // Fake DAO. Se crea el DAO dentro del test para no depender de la conexión a la base de datos, de si hay red, de si accede a un fichero...
    // En caso de usar el DOA real (CasaDaoJDBC) estaríamos hablando de prubeas de integración.
    // =========================

    static class FakeCasaDAO implements ICasaDAO {

        private List<Casa> data = new ArrayList<>();

        @Override
        public Casa obtenerCasaPorClave(int identificador) {
            return data.stream()
                    .filter(c -> c.getIdentificador()==identificador)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Casa> obtenerListaCasas() {
            return new ArrayList<>(data);
        }

        @Override
        public List<Casa> obtenerCasaPorNombre(String nombre) {
            List<Casa> resultado = new ArrayList<>();

            for (Casa c : data) {
                if (c.getNombre().equals(nombre)) {
                    resultado.add(c);
                }
            }
            return resultado;
        }

		@Override
		public void actualizarCasa(Casa Casa) {
		}

		@Override
		public void crearCasa(Casa Casa) {
            data.add(Casa);
		}

		@Override
		public void borrarCasa(Casa Casa) {
			
		}
    }
}