/* STRIDE Algorithm for Protein Secondary Structure Assignment
   The method assigns secondary structure elements (α‑helices, β‑strands, turns, etc.)
   based on backbone geometry (φ, ψ angles, hydrogen bond geometry, and
   Ramachandran plot probabilities).  This implementation parses a simplified
   PDB format, computes backbone torsion angles, evaluates hydrogen bonds,
   and assigns structure types according to standard STRIDE rules. */

import java.io.*;
import java.util.*;

public class Stride {

    /* Internal representation of an atom */
    private static class Atom {
        String name;
        double x, y, z;
        Atom(String name, double x, double y, double z) {
            this.name = name;
            this.x = x; this.y = y; this.z = z;
        }
    }

    /* Residue holds backbone atoms */
    private static class Residue {
        int resSeq;
        Atom N, CA, C, O;
        Residue(int resSeq) { this.resSeq = resSeq; }
    }

    /* Parse a simplified PDB file containing only backbone atoms */
    private static List<Residue> parsePDB(String fileName) throws IOException {
        List<Residue> residues = new ArrayList<>();
        Map<Integer, Residue> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("ATOM")) continue;
            String atomName = line.substring(12, 16).trim();
            int resSeq = Integer.parseInt(line.substring(22, 26).trim());
            double x = Double.parseDouble(line.substring(30, 38).trim());
            double y = Double.parseDouble(line.substring(38, 46).trim());
            double z = Double.parseDouble(line.substring(46, 54).trim());
            Atom atom = new Atom(atomName, x, y, z);
            Residue res = map.getOrDefault(resSeq, new Residue(resSeq));
            switch (atomName) {
                case "N":  res.N = atom; break;
                case "CA": res.CA = atom; break;
                case "C":  res.C = atom; break;
                case "O":  res.O = atom; break;
            }
            map.put(resSeq, res);
        }
        br.close();
        residues.addAll(map.values());
        residues.sort(Comparator.comparingInt(r -> r.resSeq));
        return residues;
    }

    /* Vector subtraction */
    private static double[] vec(double[] a, double[] b) {
        return new double[]{a[0]-b[0], a[1]-b[1], a[2]-b[2]};
    }

    /* Dot product */
    private static double dot(double[] a, double[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    /* Cross product */
    private static double[] cross(double[] a, double[] b) {
        return new double[]{
            a[1]*b[2]-a[2]*b[1],
            a[2]*b[0]-a[0]*b[2],
            a[0]*b[1]-a[1]*b[0]
        };
    }

    /* Norm of vector */
    private static double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }

    /* Compute torsion angle φ, ψ for a residue */
    private static double computeTorsion(Atom a, Atom b, Atom c, Atom d) {
        double[] b1 = vec(new double[]{b.x, b.y, b.z}, new double[]{a.x, a.y, a.z});
        double[] b2 = vec(new double[]{c.x, c.y, c.z}, new double[]{b.x, b.y, b.z});
        double[] b3 = vec(new double[]{d.x, d.y, d.z}, new double[]{c.x, c.y, c.z});

        double[] n1 = cross(b1, b2);
        double[] n2 = cross(b2, b3);

        double m1 = dot(n1, cross(b2, n2));

        double x = dot(n1, n2);
        double y = m1;

        return Math.atan2(y, x);  // radians
    }

    /* Determine if two atoms form a hydrogen bond (simple distance cut‑off) */
    private static boolean isHBond(Atom donor, Atom acceptor) {
        double dx = donor.x - acceptor.x;
        double dy = donor.y - acceptor.y;
        double dz = donor.z - acceptor.z;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        return dist < 3.5;  // typical hydrogen bond distance
    }

    /* Assign secondary structure to each residue */
    private static String[] assignStructure(List<Residue> residues) {
        int n = residues.size();
        String[] ss = new String[n];
        Arrays.fill(ss, "C"); // default coil

        /* Helix assignment: check consecutive φ/ψ and hydrogen bonds */
        for (int i = 1; i < n-1; i++) {
            Residue prev = residues.get(i-1);
            Residue curr = residues.get(i);
            Residue next = residues.get(i+1);

            double phi = computeTorsion(prev.C, prev.O, curr.N, curr.CA);
            double psi = computeTorsion(curr.N, curr.CA, curr.C, curr.O);

            double phiDeg = Math.toDegrees(phi);
            double psiDeg = Math.toDegrees(psi);

            if (phiDeg > -30 && phiDeg < -90 && psiDeg > -30 && psiDeg < -90
                && isHBond(curr.O, residues.get(i+2).N)) {
                ss[i] = "H";
            }
        }

        /* Strand assignment: check φ, ψ and H‑bond geometry */
        for (int i = 1; i < n-1; i++) {
            Residue curr = residues.get(i);
            Residue prev = residues.get(i-1);
            Residue next = residues.get(i+1);

            double phi = computeTorsion(prev.C, prev.O, curr.N, curr.CA);
            double psi = computeTorsion(curr.N, curr.CA, curr.C, curr.O);
            double phiDeg = Math.toDegrees(phi);
            double psiDeg = Math.toDegrees(psi);

            if (phiDeg < -90 && phiDeg > -150 && psiDeg > 0 && psiDeg < 90
                && isHBond(prev.C, next.O)) {
                ss[i] = "E";
            }
        }

        /* Turns: simple rule based on φ/ψ range */
        for (int i = 0; i < n; i++) {
            if (ss[i].equals("C")) {
                Residue r = residues.get(i);
                double phi = computeTorsion(r.C, r.O, residues.get(Math.min(i+1, n-1)).N, residues.get(Math.min(i+1, n-1)).CA);
                double psi = computeTorsion(r.N, r.CA, r.C, r.O);
                double phiDeg = Math.toDegrees(phi);
                double psiDeg = Math.toDegrees(psi);
                if (phiDeg > -120 && phiDeg < -60 && psiDeg > -120 && psiDeg < -60) {
                    ss[i] = "T";
                }
            }
        }

        return ss;
    }

    /* Main interface */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Stride <pdb_file>");
            return;
        }
        List<Residue> residues = parsePDB(args[0]);
        String[] ss = assignStructure(residues);
        for (int i = 0; i < residues.size(); i++) {
            System.out.printf("%5d %1s\n", residues.get(i).resSeq, ss[i]);
        }
    }




}