package com.lovelycatv.vertex.web3

import com.lovelycatv.vertex.web3.bip39.IWordListBIP39

class MutableMnemonics(mnemonics: List<IWordListBIP39>) : Mnemonics(mnemonics) {
    operator fun set(index: Int, value: IWordListBIP39) {
        super.words[index] = value
    }
}
