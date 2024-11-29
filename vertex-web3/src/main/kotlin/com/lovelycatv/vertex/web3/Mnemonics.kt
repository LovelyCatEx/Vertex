package com.lovelycatv.vertex.web3

import com.lovelycatv.vertex.web3.bip39.IWordListBIP39
import com.lovelycatv.vertex.web3.bip39.MnemonicBIP39
import com.lovelycatv.vertex.web3.bip39.WordListEnBIP39

open class Mnemonics {
    protected val words: MutableList<IWordListBIP39>
    private val eg: IWordListBIP39

    constructor(mnemonics: List<IWordListBIP39>) {
        require(mnemonics.isNotEmpty())
        this.eg = mnemonics[0]
        this.words = mnemonics.toMutableList()
    }

    constructor(mnemonicString: String, separator: String = " ", eg: IWordListBIP39 = WordListEnBIP39.ABANDON) {
        this.eg = eg
        this.words = mnemonicString.split(separator).map { word ->
            eg.allWords()[eg.fromWordToIndex(word)]
        }.toMutableList()
    }

    val length: Int get() = this.words.size

    val entropy: MnemonicBIP39.ENTROPY get() = MnemonicBIP39.ENTROPY.fromWordCount(this.length)

    fun toIndexes(): List<Int> {
        return this.words.map { eg.fromWordToIndex(it.getWord()) }
    }

    fun toIndexesBits(): String {
        return toIndexes().joinToString(separator = "") { it.toString(2).padStart(11, '0') }
    }

    fun toRandByteArray(): ByteArray {
        return ByteArray(this.entropy.length / 8) {
            this.toRandomBitsAndChecksumBits().first.chunked(8)[it].toUByte(2).toByte()
        }
    }

    fun toRandomBitsAndChecksumBits(): Pair<String, String> {
        return toIndexesBits().run {
            this.dropLast(entropy.checksumBitCount) to this.drop(entropy.length)
        }
    }

    operator fun get(index: Int): IWordListBIP39 {
        return this.words[index]
    }

    override fun toString(): String {
        return this.words.joinToString(" ").lowercase()
    }

    fun toMutable(): MutableMnemonics = MutableMnemonics(this.words)
}
