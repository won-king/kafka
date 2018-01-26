package com.wonking.projects.kafka.annotation;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Created by kewangk on 2018/1/19.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"spring.cloud.gateway.annotation.Destroyed"})
public class DestroyedAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("this is my first annotation processor");
        return false;
    }
}
