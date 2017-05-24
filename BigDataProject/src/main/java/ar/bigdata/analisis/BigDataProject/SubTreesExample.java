package ar.bigdata.analisis.BigDataProject;

import java.util.ArrayList;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;

public class SubTreesExample {

    public static void printSubTrees(Tree inputTree) {
        ArrayList<Word> words = new ArrayList<Word>();
        for (Tree leaf : inputTree.getLeaves()) {
            words.addAll(leaf.yieldWords());
        }
        System.out.print(inputTree.label()+"\t");
        for (Word w : words) {
            System.out.print(w.word()+ " ");
        }
        System.out.println();
        for (Tree subTree : inputTree.children()) {
            printSubTrees(subTree);
        }
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        //props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP("StanfordCoreNLP-spanish");
        String text = "yo no se que le pasa a esto si anda o no anda que loco.";
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        Tree sentenceTree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(
                TreeCoreAnnotations.TreeAnnotation.class);
        System.out.println(sentenceTree.pennString());
        printSubTrees(sentenceTree);

    }
}