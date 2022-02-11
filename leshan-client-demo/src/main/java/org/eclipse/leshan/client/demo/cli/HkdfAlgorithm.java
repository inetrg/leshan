/*******************************************************************************
 * Copyright (c) 2022 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.client.demo.cli;

import org.eclipse.californium.cose.CoseException;

import com.upokecenter.cbor.CBORObject;

/**
 * Some utility method about code HKDF Algoritm as defined at https://datatracker.ietf.org/doc/html/rfc8152#section-11.1
 * and https://datatracker.ietf.org/doc/html/rfc8152#section-12.1.2
 */
// TODO Something like this should be added to californium instead of AlgorithmID.
public enum HkdfAlgorithm {
    HKDF_HMAC_SHA_256("HKDF-SHA-256", -10, 256, 0), //
    HKDF_HMAC_SHA_512("HKDF-SHA-512", -11, 512, 0), //
    HKDF_HMAC_AES_128("HKDF-AES-128", -12, 128, 0), //
    HKDF_HMAC_AES_256("HKDF-AES-256", -13, 256, 0); //

    private final String name;
    private final int value;
    private final int cbitKey;
    private final int cbitTag;
    private final CBORObject cborValue;

    HkdfAlgorithm(String name, int value, int cbitKey, int cbitTag) {
        this.name = name;
        this.value = value;
        this.cbitKey = cbitKey;
        // TODO OSCORE I'm not sure cbitTag is needed for HKDF ?
        this.cbitTag = cbitTag;
        this.cborValue = CBORObject.FromObject(value);
    }

    public static HkdfAlgorithm fromCBOR(CBORObject obj) throws CoseException {
        if (obj == null)
            throw new CoseException("No hkdf Algorithm value Specified");
        for (HkdfAlgorithm alg : HkdfAlgorithm.values()) {
            if (obj.equals(alg.cborValue))
                return alg;
        }
        throw new CoseException(String.format("Unable to find hkdf Algorithm for CborValue %s", obj));
    }

    public static HkdfAlgorithm fromValue(int value) throws CoseException {
        for (HkdfAlgorithm alg : HkdfAlgorithm.values()) {
            if (alg.value == value)
                return alg;
        }
        throw new CoseException(String.format("Unable to find hkdf Algorithm for value %s", value));
    }

    public static HkdfAlgorithm fromName(String name) throws CoseException {
        if (name == null)
            throw new CoseException("No hkdf Algorithm name Specified");
        for (HkdfAlgorithm alg : HkdfAlgorithm.values()) {
            if (name.equals(alg.name))
                return alg;
        }
        throw new CoseException(String.format("Unable to find hkdf Algorithm with name %s", name));
    }

    public int getValue() {
        return value;
    }

    public int getKeySize() {
        return cbitKey;
    }

    public int getTagSize() {
        return cbitTag;
    }

    public CBORObject AsCBOR() {
        return cborValue;
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", name, value);
    }
}
