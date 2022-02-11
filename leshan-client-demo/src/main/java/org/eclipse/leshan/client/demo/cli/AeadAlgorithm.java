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
 * Some utility method about code AEAD Algoritm as defined at https://datatracker.ietf.org/doc/html/rfc8152#section-10
 */
// TODO Something like this should be added to californium instead of AlgorithmID.
public enum AeadAlgorithm {
    AES_GCM_128("A128GCM", 1, 128, 128), //
    AES_GCM_192("A192GCM", 2, 192, 128), //
    AES_GCM_256("A256GCM", 3, 256, 128), //
    AES_CCM_16_64_128("AES-CCM-16-64-128", 10, 128, 64), //
    AES_CCM_16_64_256("AES-CCM-16-64-256", 11, 256, 64), //
    AES_CCM_64_64_128("AES-CCM-64-64-128", 12, 128, 64), //
    AES_CCM_64_64_256("AES-CCM-64-64-256", 13, 256, 64), //
    AES_CCM_16_128_128("AES-CCM-16-128-128", 30, 128, 128), //
    AES_CCM_16_128_256("AES-CCM-16-128-256", 31, 256, 128), //
    AES_CCM_64_128_128("AES-CCM-64-128-128", 32, 128, 128), //
    AES_CCM_64_128_256("AES-CCM-64-128-256", 33, 256, 128);//

    private final String name;
    private final int value;
    private final int cbitKey;
    private final int cbitTag;
    private final CBORObject cborValue;

    AeadAlgorithm(String name, int value, int cbitKey, int cbitTag) {
        this.name = name;
        this.value = value;
        this.cbitKey = cbitKey;
        this.cbitTag = cbitTag;
        this.cborValue = CBORObject.FromObject(value);
    }

    public static AeadAlgorithm fromCBOR(CBORObject obj) throws CoseException {
        if (obj == null)
            throw new CoseException("No Aead Algorithm value Specified");
        for (AeadAlgorithm alg : AeadAlgorithm.values()) {
            if (obj.equals(alg.cborValue))
                return alg;
        }
        throw new CoseException(String.format("Unable to find Aead Algorithm for CborValue %s", obj));
    }

    public static AeadAlgorithm fromValue(int value) throws CoseException {
        for (AeadAlgorithm alg : AeadAlgorithm.values()) {
            if (alg.value == value)
                return alg;
        }
        throw new CoseException(String.format("Unable to find Aead Algorithm for value %s", value));
    }

    public static AeadAlgorithm fromName(String name) throws CoseException {
        if (name == null)
            throw new CoseException("No Aead Algorithm name Specified");
        for (AeadAlgorithm alg : AeadAlgorithm.values()) {
            if (name.equals(alg.name))
                return alg;
        }
        throw new CoseException(String.format("Unable to find Aead Algorithm with name %s", name));
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
