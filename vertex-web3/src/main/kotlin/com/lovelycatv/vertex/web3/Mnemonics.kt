package com.lovelycatv.vertex.web3

import com.lovelycatv.vertex.web3.bip39.IWordListBIP39
import com.lovelycatv.vertex.web3.bip39.WordListEnBIP39

open class Mnemonics {
    protected val words: MutableList<IWordListBIP39>

    constructor(mnemonics: List<IWordListBIP39>) {
        this.words = mnemonics.toMutableList()
    }

    constructor(mnemonicString: String, separator: String = " ", eg: IWordListBIP39 = WordListEnBIP39.ABANDON) {
        this.words = mnemonicString.split(separator).map { word ->
            eg.allWords()[eg.fromWordToIndex(word)]
        }.toMutableList()
    }

    val length: Int get() = this.words.size

    operator fun get(index: Int): IWordListBIP39 {
        return this.words[index]
    }

    override fun toString(): String {
        return this.words.joinToString(" ")
    }

    fun toMutable(): MutableMnemonics = MutableMnemonics(this.words)
}
