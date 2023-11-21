package com.github.cd721.data_leakage_plugin.inlays

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.declarative.*
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.*

@Suppress("UnstableApiUsage")
class Collector() : SharedBypassCollector {
    override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
        sink.addPresentation(InlineInlayPosition(
                element.textRange.endOffset,true

        ),
                hasBackground = true){
            text("Data Leakage!")
        }
    }


}