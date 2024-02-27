package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.LeakageQuickFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.find.FindManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.util.DocumentUtil;
import com.jetbrains.python.psi.PyPsiFacade;
import org.jetbrains.annotations.NotNull;

 class MultiTestLeakageQuickFix extends LeakageQuickFix<MultiTestLeakageInstance> {
    @NotNull
    @Override
    public String getName() {
        return InspectionBundle.get("inspectionText.removeRedundantTestEvaluations.quickfix.text");
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        var myFacade = PyPsiFacade.getInstance(project);
        var instance = descriptor.getPsiElement();
        var myFactory = RefactoringFactory.getInstance(project);

        //PyReference expression is not a PsiNamedElement, nor does it have writeable metadata

        //  var references = instance.getReferences();
        //does not get all references
//            PsiElement parentOfType = PsiTreeUtil.getParentOfType(instance, instance.getClass());
        //   var references = ReferencesSearch.search(parentOfType);
//
//            var references = instance.getReferences();//Does not get references
        //            var usages = myRefactoring.findUsages();//Does not find any usages
//

        //  PsiNamedElement namedElement = PsiTreeUtil.getParentOfType(instance, PsiNamedElement.class);

//            var myRefactoring = myFactory.createRename((PsiElement) instance, instance.getText() + "_1",
//                    true, true);
//            var usages = myRefactoring.findUsages();
//            var processor = RenamePyElementProcessor.forElement(instance);
//            processor.renameElement(instance, instance.getText()+"_1", usages,null);
//


//
//            ApplicationManager.getApplication().executeOnPooledThread(()->{
//                ApplicationManager.getApplication().invokeLater(()->{
//                    myRefactoring.run();
//                });
//            });


//            CommandProcessor.getInstance().executeCommand(project, new Runnable() {
//                @Override
//                public void run() {
//                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            myRefactoring.run();
//                        }
//                    });
//                }
//            }, "Rename", "Rename action");


//            myRefactoring.doRefactoring(usages); //Cannot perform refactoring and throws an error
//            // java.lang.Throwable: Unknown element type : PyReferenceExpression: X_test

        FindManager myFindManager = FindManager.getInstance(project);
        var possible = myFindManager.canFindUsages(instance);


        var descriptionText = descriptor.getDescriptionTemplate();
        var psiElement = descriptor.getPsiElement();
        var psiFile = psiElement.getContainingFile();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = documentManager.getDocument(psiFile);
        var lineNumber = descriptor.getLineNumber();

        int offset = document.getLineStartOffset(lineNumber);
        //    document.replaceString(instance.getTextOffset(), instance.getTextOffset() + instance.getTextLength(), instance.getText() + "_1");

        for (int i = 0; i < multiTestLeakageInstances.size(); i++) {
            //Doesn't always reload contents of document from disk
            var inst = multiTestLeakageInstances.get(i);
            var line = inst.lineNumber()-1;
            var lineTextRange = DocumentUtil.getLineTextRange(document, line);
            var lineContent = document.getText(lineTextRange);
            var newStr = lineContent.replace(instance.getText(), instance.getText() + "_" + i);
            document.replaceString(document.getLineStartOffset(line),document.getLineEndOffset(line),newStr);
        }

    }
}