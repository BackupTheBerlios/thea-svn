package fr.unice.bioinfo.thea.classification;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.unice.bioinfo.allonto.datamodel.Resource;

public class Score {

    private Resource term;

    private double score;

    private boolean overExpressed; // true if the term is overexpressed

    private int clusterSize; // cluster size

    private int numberOfGenes; // nb genes associated with a specific term

    private int populationSize; // total number of genes

    private int favorableCaseInPopulation; // total number of genes associated

    // with a specific term

    public Score(Resource term, double score, boolean over, int nbOfSuccess,
            int nbOfEvent, int favorableCaseInPopulation, int populationSize) {
        this.term = term;
        this.score = score;
        this.overExpressed = over;
        this.clusterSize = nbOfEvent;
        this.numberOfGenes = nbOfSuccess;
        this.populationSize = populationSize;
        this.favorableCaseInPopulation = favorableCaseInPopulation;
    }

    public Resource getTerm() {
        return term;
    }

    public double getScore() {
        return score;
    }

    public void putScore(double score) {
        this.score = score;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public int getNbAssociatedGenesInCluster() {
        return numberOfGenes;
    }

    public int getpopulationSize() {
        return populationSize;
    }

    public int getNbAssociatedGenesInPopulation() {
        return favorableCaseInPopulation;
    }

    public boolean isOverexpressed() {
        return overExpressed;
    }

    public String getFormattedScore() {
        if (score < 1) {
            NumberFormat formatter = new DecimalFormat("0.###E0");

            return formatter.format(score);
        }

        return String.valueOf(score);
    }
}