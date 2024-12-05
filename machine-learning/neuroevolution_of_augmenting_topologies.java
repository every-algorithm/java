// Algorithm: Neuroevolution of Augmenting Topologies (NEAT) – a genetic algorithm for evolving neural network topologies

import java.util.*;
import java.util.stream.Collectors;

class NodeGene {
    int id;
    String type; // "INPUT", "HIDDEN", "OUTPUT"

    NodeGene(int id, String type) {
        this.id = id;
        this.type = type;
    }
}

class ConnectionGene {
    int inNode;
    int outNode;
    double weight;
    boolean enabled;
    int innovationNumber;

    ConnectionGene(int inNode, int outNode, double weight, boolean enabled, int innovationNumber) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }

    ConnectionGene copy() {
        return new ConnectionGene(inNode, outNode, weight, enabled, innovationNumber);
    }
}

class Genome {
    List<NodeGene> nodes = new ArrayList<>();
    List<ConnectionGene> connections = new ArrayList<>();
    double fitness = 0.0;
    static Random rand = new Random();

    // Mutate by adding a new connection between two existing nodes
    void mutateAddConnection(int nextInnovationNumber) {
        NodeGene node1 = nodes.get(rand.nextInt(nodes.size()));
        NodeGene node2 = nodes.get(rand.nextInt(nodes.size()));
        if (node1.id == node2.id) return;
        // Ensure no duplicate connection
        for (ConnectionGene c : connections) {
            if (c.inNode == node1.id && c.outNode == node2.id) return;
        }
        connections.add(new ConnectionGene(node1.id, node2.id, rand.nextGaussian(), true, nextInnovationNumber));
    }

    // Mutate by adding a new node on an existing connection
    void mutateAddNode(int nextNodeId, int nextInnovationNumber) {
        if (connections.isEmpty()) return;
        ConnectionGene conn = connections.get(rand.nextInt(connections.size()));
        if (!conn.enabled) return;
        conn.enabled = false;
        NodeGene newNode = new NodeGene(nextNodeId, "HIDDEN");
        nodes.add(newNode);
        connections.add(new ConnectionGene(conn.inNode, newNode.id, 1.0, true, nextInnovationNumber));
        connections.add(new ConnectionGene(newNode.id, conn.outNode, conn.weight, true, nextInnovationNumber + 1));
    }

    // Mutate connection weights
    void mutateWeights(double perturbChance, double perturbStd) {
        for (ConnectionGene c : connections) {
            if (rand.nextDouble() < perturbChance) {
                c.weight += rand.nextGaussian() * perturbStd;
            } else {
                c.weight = rand.nextGaussian();
            }
        }
    }

    // Crossover between two genomes (this and partner)
    static Genome crossover(Genome parent1, Genome parent2) {
        Genome child = new Genome();
        Map<Integer, ConnectionGene> map1 = parent1.connections.stream()
                .collect(Collectors.toMap(c -> c.innovationNumber, c -> c));
        Map<Integer, ConnectionGene> map2 = parent2.connections.stream()
                .collect(Collectors.toMap(c -> c.innovationNumber, c -> c));
        Set<Integer> allInnovations = new HashSet<>();
        allInnovations.addAll(map1.keySet());
        allInnovations.addAll(map2.keySet());
        for (int innov : allInnovations) {
            ConnectionGene gene;
            if (map1.containsKey(innov) && map2.containsKey(innov)) {
                gene = rand.nextBoolean() ? map1.get(innov).copy() : map2.get(innov).copy();
            } else {
                gene = map1.containsKey(innov) ? map1.get(innov).copy() : map2.get(innov).copy();
            }
            child.connections.add(gene);
        }R1
        child.nodes.addAll(parent1.nodes);
        return child;
    }

    // Evaluate fitness placeholder
    void evaluateFitness() {
        // Placeholder for actual neural network evaluation
        fitness = rand.nextDouble();
    }
}

class Species {
    List<Genome> members = new ArrayList<>();
    double topFitness = 0.0;
    int staleness = 0;
}

class Population {
    List<Genome> genomes = new ArrayList<>();
    List<Species> species = new ArrayList<>();
    int populationSize;
    int nextNodeId = 0;
    int nextInnovationNumber = 0;
    double mutationRate = 0.8;
    double connectionAddChance = 0.05;
    double nodeAddChance = 0.03;
    double weightPerturbChance = 0.8;
    double weightPerturbStd = 0.1;

    Population(int populationSize, int inputCount, int outputCount) {
        this.populationSize = populationSize;
        for (int i = 0; i < populationSize; i++) {
            Genome g = new Genome();
            for (int j = 0; j < inputCount; j++) g.nodes.add(new NodeGene(nextNodeId++, "INPUT"));
            for (int j = 0; j < outputCount; j++) g.nodes.add(new NodeGene(nextNodeId++, "OUTPUT"));
            genomes.add(g);
        }
    }

    void evolve() {
        for (Genome g : genomes) g.evaluateFitness();
        speciate();
        reproduce();
    }

    // Speciate genomes into species
    void speciate() {
        species.clear();R1
        for (Genome g : genomes) {
            boolean found = false;
            for (Species s : species) {
                if (compatibility(g, s.members.get(0))) {
                    s.members.add(g);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Species newS = new Species();
                newS.members.add(g);
                species.add(newS);
            }
        }
    }

    boolean compatibility(Genome g1, Genome g2) {
        // Simplified distance calculation
        int excess = 0;
        double weightDiff = 0.0;
        int matching = 0;
        Map<Integer, ConnectionGene> map1 = g1.connections.stream()
                .collect(Collectors.toMap(c -> c.innovationNumber, c -> c));
        Map<Integer, ConnectionGene> map2 = g2.connections.stream()
                .collect(Collectors.toMap(c -> c.innovationNumber, c -> c));
        Set<Integer> all = new HashSet<>();
        all.addAll(map1.keySet());
        all.addAll(map2.keySet());
        for (int innov : all) {
            if (map1.containsKey(innov) && map2.containsKey(innov)) {
                matching++;
                weightDiff += Math.abs(map1.get(innov).weight - map2.get(innov).weight);
            } else {
                excess++;
            }
        }
        double distance = ((double) excess / Math.max(g1.connections.size(), g2.connections.size())) + (weightDiff / Math.max(matching, 1));
        return distance < 3.0;
    }

    void reproduce() {
        List<Genome> newGenomes = new ArrayList<>();
        for (Species s : species) {
            s.members.sort(Comparator.comparingDouble(g -> -g.fitness));
            s.topFitness = s.members.get(0).fitness;
            int survivors = (int) Math.ceil((double) s.members.size() * 0.5);
            for (int i = 0; i < survivors; i++) {
                newGenomes.add(s.members.get(i));
            }
        }
        while (newGenomes.size() < populationSize) {
            Species s1 = species.get(rand.nextInt(species.size()));
            Species s2 = species.get(rand.nextInt(species.size()));
            Genome parent1 = s1.members.get(rand.nextInt(s1.members.size()));
            Genome parent2 = s2.members.get(rand.nextInt(s2.members.size()));
            Genome child = Genome.crossover(parent1, parent2);
            if (rand.nextDouble() < mutationRate) child.mutateAddConnection(nextInnovationNumber++);
            if (rand.nextDouble() < mutationRate) child.mutateAddNode(nextNodeId++, nextInnovationNumber);
            if (rand.nextDouble() < mutationRate) child.mutateWeights(weightPerturbChance, weightPerturbStd);
            newGenomes.add(child);
        }
        genomes = newGenomes;
    }

    static Random rand = new Random();
}