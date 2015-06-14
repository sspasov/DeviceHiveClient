package com.devicehive.sspasov.client.objects;

/**
 * Created by toni on 23.05.15.
 */
public class Parameter {
    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    public final String name;
    public final String value;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }
}