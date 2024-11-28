package com.lovelycatv.vertex.web3.bip39

import com.alibaba.fastjson2.JSON
import com.lovelycatv.vertex.web3.Mnemonics
import java.security.MessageDigest
import java.security.SecureRandom

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
        ENT_256(256)
    }

    private fun generateEntropy(entLength: ENTROPY): ByteArray {
        val byteLength = entLength.length / 8
        val random = SecureRandom()
        return ByteArray(byteLength).also { random.nextBytes(it) }.also { println(JSON.toJSONString(it)) }
    }

    private fun calculateChecksum(entropy: ByteArray): Int {
        val hash = MessageDigest.getInstance("SHA-256").digest(entropy)
        val checksumBits = entropy.size * 8 / 32
        val checksum = hash[0].toInt() and 0xFF
        return checksum.shr(8 - checksumBits)
    }

    private fun combineEntropyAndChecksum(entropy: ByteArray, checksum: Int): String {
        val entropyBits = entropy.joinToString("") {
            it.toUByte().toString(2).padStart(8, '0')
        }
        val checksumBits = checksum.toString(2).padStart(entropy.size * 8 / 32, '0')
        return entropyBits + checksumBits
    }

    private fun splitBitsToIndices(bits: String): List<Int> {
        return bits.chunked(11).map { it.toInt(2) }
    }

    private fun indicesToMnemonic(indices: List<Int>): Mnemonics {
        return Mnemonics(indices.map { WordListEnBIP39.entries[it] })
    }

    fun generateMnemonic(entLength: ENTROPY): Mnemonics {
        val entropy = generateEntropy(entLength)
        val checksum = calculateChecksum(entropy)
        val combinedBits = combineEntropyAndChecksum(entropy, checksum)
        val indices = splitBitsToIndices(combinedBits)
        return indicesToMnemonic(indices)
    }
}