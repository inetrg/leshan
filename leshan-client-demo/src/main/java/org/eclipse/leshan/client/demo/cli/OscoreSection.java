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

import java.util.Arrays;

import org.eclipse.californium.cose.CoseException;
import org.eclipse.leshan.core.util.StringUtils;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.TypeConversionException;

/**
 * Command line Section about OSCORE credentials.
 */
public class OscoreSection {

    // mandatory parameters
    @Option(required = true,
            names = { "-sid", "--sender-id" },
            description = { "Byte string used to identify the Sender Context, to"
                    + " derive AEAD keys and Common IV, and to contribute to the"
                    + " uniqueness of AEAD nonces.  Maximum length is determined by the AEAD Algorithm." })
    public String senderId;

    @Option(required = true,
            names = { "-msec", "--master-secret" },
            description = { "Variable length, random byte string used to derive AEAD keys and Common IV." })
    public String masterSecret;

    @Option(required = true,
            names = { "-rid", "--recipient-id" },
            description = { "Byte string used to identify the Recipient Context,"
                    + " to derive AEAD keys and Common IV, and to contribute to the"
                    + " uniqueness of AEAD nonces.  Maximum length is determined by the AEAD Algorithm." })
    public String recipientId;

    // optional parameters
    @Option(required = true,
            names = { "-aead", "--aead-algorithm" },
            description = { "The COSE AEAD algorithm to use for encryption.", "Default is ${DEFAULT-VALUE}." },
            defaultValue = "AES-CCM-16-64-128",
            converter = AeadAlgorithmConverter.class)
    public Integer aeadAlgorithm;

    private static class AeadAlgorithmConverter implements ITypeConverter<Integer> {
        @Override
        public Integer convert(String s) {
            try {
                if (StringUtils.isNumeric(s)) {
                    // Indicated as integer
                    return AeadAlgorithm.fromValue(Integer.parseInt(s)).getValue();
                } else {
                    // Indicated as string
                    return AeadAlgorithm.fromName(s).getValue();
                }
            } catch (NumberFormatException | CoseException e) {
                throw new TypeConversionException(String.format("%s \nSupported AEAD algorithm are %s.", e.getMessage(),
                        Arrays.toString(AeadAlgorithm.values())));
            }
        }
    };

    @Option(required = true,
            names = { "-msalt", "--master-salt" },
            description = {
                    "Optional variable-length byte string containing the salt used to derive AEAD keys and Common IV.",
                    "Default is an empty string." },
            defaultValue = "")
    public String masterSalt;

    @Option(required = true,
            names = { "-hkdf", "--hkdf-algorithm" },
            description = {
                    "An HMAC-based key derivation function used to derive the Sender Key, Recipient Key, and Common IV.",
                    "Default is ${DEFAULT-VALUE}." },
            defaultValue = "HKDF-SHA-256",
            converter = hkdfAlgorithmConverter.class)
    public Integer hkdfAlgorithm;

    private static class hkdfAlgorithmConverter implements ITypeConverter<Integer> {
        @Override
        public Integer convert(String s) {
            try {
                if (s.matches("-?\\d+")) {
                    // Indicated as integer
                    return HkdfAlgorithm.fromValue(Integer.parseInt(s)).getValue();
                } else {
                    // Indicated as string
                    return HkdfAlgorithm.fromName(s).getValue();
                }
            } catch (NumberFormatException | CoseException e) {
                throw new TypeConversionException(String.format("%s \nSupported HKDF algorithm are %s.", e.getMessage(),
                        Arrays.toString(HkdfAlgorithm.values())));
            }
        }
    };

    // ---------------------------------------------------------------------------------------//
    // TODO OSCORE I don't know if we need to add an option for anti-replay,
    // maybe this will be more an californium config, let's skip it for now.
    //
    // @Option(required = true, names = { "-rw", "--replay-windows" }, description = { "TBD." })
    // public String replayWindows;
    // ---------------------------------------------------------------------------------------//
}
