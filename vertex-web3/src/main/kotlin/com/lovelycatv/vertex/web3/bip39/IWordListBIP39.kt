package com.lovelycatv.vertex.web3.bip39

/**
 * @author lovelycat
 * @since 2024-11-28 12:44
 * @version 1.0
 */
interface IWordListBIP39 {
    fun allWords(): List<IWordListBIP39>

    fun getWord(): String

    fun fromWordToIndex(word: String): Int
}