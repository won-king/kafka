package com.wonking.projects.kafka.annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by kewangk on 2018/1/20.
 */
public class InterfaceExtractorProcessor implements AnnotationProcessor {
    private final AnnotationProcessorEnvironment env;
    private ArrayList<MethodDeclaration> ifcMethods=new ArrayList<>();
    public InterfaceExtractorProcessor(AnnotationProcessorEnvironment e){
        env=e;
    }

    @Override
    public void process() {
        for(TypeDeclaration type: env.getSpecifiedTypeDeclarations()){
            ExtractInterface annot=type.getAnnotation(ExtractInterface.class);
            if(annot==null){
                break;
            }
            for(MethodDeclaration m: type.getMethods()){
                if(m.getModifiers().contains(Modifier.PUBLIC) && !m.getModifiers().contains(Modifier.STATIC)){
                    ifcMethods.add(m);
                }
            }
            if(ifcMethods.size()>0){
                try{
                    PrintWriter pw=env.getFiler().createSourceFile(annot.value());
                    pw.println("package "+type.getPackage().getQualifiedName()+";");
                    pw.println("public interface "+annot.value()+"{");
                    for(MethodDeclaration md: ifcMethods){
                        pw.print(md.getReturnType()+" ");
                        pw.print(md.getSimpleName()+" (");
                        int i=0;
                        for(ParameterDeclaration pd: md.getParameters()){
                            pw.print(pd.getType()+" "+pd.getSimpleName());
                            if(++i < md.getParameters().size()){
                                pw.print(", ");
                            }
                        }
                        pw.println(");");
                    }
                    pw.println("}");
                    pw.close();
                }catch (IOException e){
                    System.out.println("generate exception");
                }
            }
        }
    }
}
