package com.lovelycatv.vertex.web3.bip39

import com.alibaba.fastjson2.JSON
import com.lovelycatv.vertex.web3.Mnemonics
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * @author lovelycat
 * @since 2024-11-26 12:12
 * @version 1.0
 */
object MnemonicBIP39 {
    enum class ENTROPY(val length: Int) {
        ENT_128(128),
        ENT_160(160),
        ENT_192(192),
        ENT_224(224),
        ENT_256(256);

        val checksumBitCount: Int get() = (this.length / 32)

        val indexesBitCount: Int get() = this.length + this.checksumBitCount

        val indexBitCount: Int get() = indexesBitCount / 11

        companion object {
            fun fromWordCount(count: Int): ENTROPY {
                return when (count) {
                    12 -> ENT_128
                    15 -> ENT_160
                    18 -> ENT_192
                    21 -> ENT_224
                    25 -> ENT_256
                    else -> throw IllegalArgumentException("Word count must be 12 / 15 / 18 / 21 / 24")
                }
            }
        }
    }

    /**
     * Generate a random byte array
     *
     * @param entLength ENTROPY
     * @return Random ByteArray
     */
    fun generateEntropy(entLength: ENTROPY): ByteArray {
        val byteLength = entLength.length / 8
        val random = SecureRandom()
        return ByteArray(byteLength).also { random.nextBytes(it) }
    }

    /**
     * Calculate checksum number according to the entropy byte array
     *
     * @param entropy Entropy ByteArray
     * @return Checksum
     */
    fun calculateChecksum(entropy: ByteArray): Int {
        val hash = MessageDigest.getInstance("SHA-256").digest(entropy)
        val checksumBits = entropy.size * 8 / 32
        val checksum = hash[0].toInt() and 0xFF
        return checksum.shr(8 - checksumBits)
    }

    /**
     * Combine entropy and checksum to a single bits string
     *
     * @param entropy Entropy ByteArray
     * @param checksum Checksum
     */
    fun combineEntropyAndChecksum(entropy: ByteArray, checksum: Int): String {
        val entropyBits = entropy.joinToString("") {
            it.toUByte().toString(2).padStart(8, '0')
        }
        val checksumBits = checksum.toString(2).padStart(entropy.size * 8 / 32, '0')
        return entropyBits + checksumBits
    }

    fun splitBitsToIndices(bits: String): List<Int> {
        return bits.chunked(11).map { it.toInt(2) }
    }

    fun indicesToMnemonic(indices: List<Int>): Mnemonics {
        return Mnemonics(indices.map { WordListEnBIP39.entries[it] })
    }

    /**
     * Generate a random [Mnemonics]
     *
     * @param entLength ENTROPY
     * @return [Mnemonics]
     */
    fun generateMnemonic(entLength: ENTROPY): Mnemonics {
        val entropy = generateEntropy(entLength)
        val checksum = calculateChecksum(entropy)
        val combinedBits = combineEntropyAndChecksum(entropy, checksum)
        val indices = splitBitsToIndices(combinedBits)
        return indicesToMnemonic(indices)
    }

    /**
     * Generate bip39 seed,
     * Eg: network drama crash paper hamster among spot when vendor slab web group (No Password),
     * Seed: 2d3ecb5f775e788ff70e11d2690f0036faea3a80dfb3e22aeda345371fc3d767a4daec80eb8e01ce6401e656dec841cc4315909daf3731ad5dedf8064f03bdec
     *
     * @param mnemonics Mnemonics
     * @param password String Default empty
     * @return BIP39 Seed
     */
    fun generateSeed(mnemonics: Mnemonics, password: String = ""): ByteArray {
        val salt = "mnemonic$password".toByteArray()
        val iterations = 2048
        val keyLength = 512

        val pbkdf2 = PBEKeySpec(mnemonics.toString().toCharArray(), salt, iterations, keyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        return secretKeyFactory.generateSecret(pbkdf2).encoded
    }
}