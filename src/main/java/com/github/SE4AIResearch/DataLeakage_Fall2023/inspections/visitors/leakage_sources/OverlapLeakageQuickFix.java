package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;


import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

//private class OverlapLeakageQuickFix implements LocalQuickFix {
//
//
//        public OverlapLeakageQuickFix() {
//        }
//
//        @NotNull
//        @Override
//        public String getName() {
//            return InspectionBundle.get("inspectionText.swapSplitAndSample.quickfix.text");
//        }
//
//        @Override
//        public @IntentionFamilyName @NotNull String getFamilyName() {
//            return getName();
//        }
//
//        @Override
//        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//
//            var lineNumber = descriptor.getLineNumber() + 1;//was off by one
//
//            var descriptionText = descriptor.getDescriptionTemplate();
//            var psiElement = descriptor.getPsiElement();
//            var psiFile = psiElement.getContainingFile();
//            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
//            Document document = documentManager.getDocument(psiFile);
//            //Split
//            if (descriptionText.equals(InspectionBundle.get("inspectionText.splitBeforeSampleReminder.text"))) {
//
//            }
//            //Source not linked to instance
//
//            //Sample
//            if (descriptionText.equals(InspectionBundle.get("inspectionText.overlapLeakage.text"))
//                    && psiElement.getText().contains(OverlapLeakageSourceKeyword.sample.toString())) {
//
//            }
//
////won't work if assignment is split on multiple lines
//            var instance = getInstanceForLeakageSourceAssociatedWithNode(overlapLeakageInstances, psiElement);
//            var source = instance.getLeakageSource();
//            if (source.getCause().equals(LeakageCause.SplitBeforeSample)) {
//                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
//
//                int offset = document.getLineStartOffset(lineNumber - 1);
//
//                @Nullable
//                PsiElement firstElementOnLine = psiFile.findElementAt(offset
//                );
//                PsiManager manager = PsiManager.getInstance(project);
//                var myFacade = PyPsiFacade.getInstance(project);
//
//                var lineContentOfSplitCall = holder.getResults().stream().map(
//                        problem -> problem.getPsiElement().getParent().getText()
//                ).filter(taint -> taint.toLowerCase().contains("split")).findFirst().get();
//
//                var offsetOfSplitCall = holder.getResults().stream().map(
//                                problem -> problem.getPsiElement().getParent()
//                        ).filter(taint -> taint.getText().toLowerCase().contains("split"))
//                        .map(taint -> taint.getTextOffset()).filter(splitOffset -> splitOffset > offset).findFirst().get();
//
//                document.replaceString(offsetOfSplitCall, offsetOfSplitCall +
//                        lineContentOfSplitCall.length(), "");
//
//                document.insertString(offset, lineContentOfSplitCall + "\n");
//
//
//                //Remove split sample from leakage instances
//                //  removeInstance(instance);
//
//                var lineNumbersToRemove = new ArrayList<Integer>();
//                lineNumbersToRemove.add(document.getLineNumber(offset));
//                lineNumbersToRemove.add(document.getLineNumber(lineNumber - 1));
//                lineNumbersToRemove.add(document.getLineNumber(offset) + 1);
//                lineNumbersToRemove.add(document.getLineNumber(offsetOfSplitCall) + 1);
////                overlapLeakageInstances.stream().forEach(thisInstance ->
////                        thisInstance.getLeakageSource().removeLineNumbers(lineNumbersToRemove));
//
//                InspectionUtils.addLinesToExclusion(lineNumbersToRemove);
//                DaemonCodeAnalyzer.getInstance(project).restart();
//            }
//
//
//        }
//    }
