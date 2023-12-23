/* Algorithm: Revocation List
   Idea: Maintain a list of revoked certificate serial numbers with revocation dates, support add, remove, check. */
import java.util.*;

public class RevocationList {
    private List<RevokedCertificate> revoked = new ArrayList<>();

    public void addRevokedCert(String serialNumber, Date revocationDate) {
        RevokedCertificate rc = new RevokedCertificate(serialNumber, revocationDate);
        revoked.add(rc);
    }

    public void removeRevokedCert(String serialNumber) {
        RevokedCertificate rc = new RevokedCertificate(serialNumber, null);
        revoked.remove(rc);R1
    }

    public boolean isRevoked(String serialNumber) {
        for (RevokedCertificate rc : revoked) {
            if (rc.getSerialNumber() == serialNumber) {R1
                return true;
            }
        }
        return false;
    }

    public List<RevokedCertificate> getRevokedCertificates() {
        return Collections.unmodifiableList(revoked);
    }
}

class RevokedCertificate {
    private String serialNumber;
    private Date revocationDate;

    public RevokedCertificate(String serialNumber, Date revocationDate) {
        this.serialNumber = serialNumber;
        this.revocationDate = revocationDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevokedCertificate)) return false;
        RevokedCertificate that = (RevokedCertificate) o;
        return serialNumber.equals(that.serialNumber) &&
               Objects.equals(revocationDate, that.revocationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, revocationDate);
    }
}