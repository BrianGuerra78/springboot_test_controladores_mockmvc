package org.bguerra.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.bguerra.test.springboot.app.Datos.*;

import org.bguerra.test.springboot.app.exceptions.DineroInsuficienteException;
import org.bguerra.test.springboot.app.models.Banco;
import org.bguerra.test.springboot.app.models.Cuenta;
import org.bguerra.test.springboot.app.repositories.BancoRepository;
import org.bguerra.test.springboot.app.repositories.CuentaRepository;
import org.bguerra.test.springboot.app.services.CuentaService;
import org.bguerra.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

    //@Mock
    @MockBean
    CuentaRepository cuentaRepository;
    //@Mock
    @MockBean
    BancoRepository bancoRepository;
    /*@InjectMocks
    CuentaServiceImpl cuentaService;*/
    @Autowired//para eso se debe de importar @Service en la clase CuentaServiceImpl
            CuentaService cuentaService; //springboot permite detectar que calse o componente tiene la interface

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepository.class);
        bancoRepository = mock(BancoRepository.class);
        cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
        //para que los datos no se afecten por cada prueba unitaria y tengan los datos intactos
        /*Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
        Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
        Datos.BANCO.setTotalTransferencias(0);*/
        //es una forma de hacerlo
    }

    @Test
    void contextLoads() {
        /*when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);
        when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002);
        when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO);*/

        /*when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());*/

        //de esta manera de usan en toda la clase
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);
        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        cuentaService.transferir(1L, 2L, new BigDecimal("100"), 1L);
        saldoOrigen = cuentaService.revisarSaldo(1L);
        saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("900", saldoOrigen.toPlainString());
        assertEquals("2100", saldoDestino.toPlainString());

        int total = cuentaService.revisarTotalTransferencias(1L);
        assertEquals(1, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        //verify(cuentaRepository, times(2)).update(any(Cuenta.class));
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        //verify(bancoRepository).update(any(Banco.class));
        verify(bancoRepository).save(any(Banco.class));

        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);
        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        assertThrows(DineroInsuficienteException.class, () -> {
            cuentaService.transferir(1L, 2L, new BigDecimal("1200"), 1L);
        });
        saldoOrigen = cuentaService.revisarSaldo(1L);
        saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        int total = cuentaService.revisarTotalTransferencias(1L);
        assertEquals(0, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(2)).findById(2L);
        //verify(cuentaRepository, never()).update(any(Cuenta.class));
        verify(cuentaRepository, never()).save(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        //verify(bancoRepository, never()).update(any(Banco.class));
        verify(bancoRepository, never()).save(any(Banco.class));

        verify(cuentaRepository, times(5)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads3() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

        Cuenta cuenta1 = cuentaService.findById(1L);
        Cuenta cuenta2 = cuentaService.findById(1L);

        assertSame(cuenta1, cuenta2);
        assertTrue(cuenta1 == cuenta2);
        assertEquals("Brian", cuenta1.getPersona());
        assertEquals("Brian", cuenta2.getPersona());
        verify(cuentaRepository, times(2)).findById(1L);
    }

    @Test
    void testFindAll() {
        //Given
        List<Cuenta> datos = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
        when(cuentaRepository.findAll()).thenReturn(datos);
        //When
        List<Cuenta> cuentas = cuentaService.findAll();
        //Then
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

        verify(cuentaRepository).findAll();
    }

    @Test
    void testSave() {
        //Given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaRepository.save(any())).then(invocationOnMock -> {
            Cuenta c = invocationOnMock.getArgument(0);
            c.setId(3L);
            return c;
        });
        //When
        Cuenta cuenta = cuentaService.save(cuentaPepe);
        //Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals(3, cuenta.getId());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        verify(cuentaRepository).save(any());
    }
}
