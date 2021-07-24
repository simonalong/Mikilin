package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.AutoCheck;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * @author shizi
 * @since 2021-07-22 23:36:11
 */
@SupportedAnnotationTypes("com.simonalong.mikilin.annotation.AutoCheck")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MGetterProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //        StringBuilder codeBuilder = new StringBuilder();
        //        // package
        //        codeBuilder.append("package com.eog.api.impl;");
        //
        //        // import
        //        codeBuilder.append("package com.eog.api.impl;");
        //
        //        // public class
        //        codeBuilder.append("public class GenerateClass{}");
        //
        //
        //        try {
        //            JavaFileObject source = processingEnv.getFiler().createClassFile("com.eog.api.impl.GenerateClass");
        //            Writer writer = source.openWriter();
        //            writer.write(codeBuilder.toString());
        //            writer.flush();
        //            writer.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AutoCheck.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    List<JCTree.JCMethodDecl> jcVariableDeclList = List.nil();

                    for (JCTree tree : jcClassDecl.defs) {
                        if (tree.getKind().equals(Tree.Kind.METHOD)) {
                            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;
                            for (JCTree.JCVariableDecl parameter : jcMethodDecl.getParameters()) {
                                System.out.println(parameter.pos);
                                // JCModifiers mods,
                                //Name name,
                                //List<JCTypeParameter> typarams,
                                //JCTree extending,
                                //List<JCExpression> implementing,
                                //List<JCTree> defs)
                                //                                jcClassDecl.defs = jcClassDecl.defs.prepend(treeMaker.ClassDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString("GenerateEntity"), List.nil(), parameter.vartype, List.nil(), List.nil()));

                                ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                                statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), parameter.getName())));
                                JCTree.JCBlock body = treeMaker.Block(0, statements.toList());

                                JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString("getTTT"), parameter.vartype, List.nil(), List.nil(), List.nil(), body, null);
                                jcClassDecl.defs = jcClassDecl.defs.prepend(methodDecl);
                            }
                            jcVariableDeclList = jcVariableDeclList.append(jcMethodDecl);
                        }
                    }

                    //                    jcVariableDeclList.forEach(jcVariableDecl -> {
                    //                        messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                    //                        jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl));
                    //                    });
                    super.visitClassDef(jcClassDecl);
                }
            });
        });

        return true;
    }

    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName())));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());


        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body,
            null);
    }

    private Name getNewMethodName(Name name) {
        String s = name.toString();
        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }
}
