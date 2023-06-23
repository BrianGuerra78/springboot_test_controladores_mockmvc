package org.bguerra.test.springboot.app;

import org.bguerra.test.springboot.app.models.Banco;
import org.bguerra.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    /*public static final Cuenta CUENTA_001 = new Cuenta(1L, "Brian", new BigDecimal("1000"));
    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Jhon", new BigDecimal("2000"));
    public static final Banco BANCO = new Banco(1L, "El banco financiero", 0);*/
    //segnda forma para que no se modifiquen los datos al realizar los test

    public static Optional<Cuenta> crearCuenta001(){
        return Optional.of(new Cuenta(1L, "Brian", new BigDecimal("1000")));
    }

    public static Optional<Cuenta>  crearCuenta002(){
        return Optional.of(new Cuenta(2L, "Jhon", new BigDecimal("2000")));
    }
    public static Optional<Banco> crearBanco(){
        return Optional.of(new Banco(1L, "El banco financiero", 0));
    }
}
