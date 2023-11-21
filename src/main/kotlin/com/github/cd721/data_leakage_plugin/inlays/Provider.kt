package com.github.cd721.data_leakage_plugin.inlays

import com.intellij.codeInsight.hints.declarative.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class Provider : InlayHintsProvider {
    companion object {
        const val PROVIDER_ID : String = "myProvider"
    }
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {

        return Collector()
    }


}