package rbac.framework.object;

/**
 * 权限
 */
public class Permissions {

    /**
     * 权限标识符
     */
    private String permissiions;

    public String getPermissiions() {
        return permissiions;
    }

    public void setPermissiions(String permissiions) {
        this.permissiions = permissiions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((permissiions == null) ? 0 : permissiions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permissions other = (Permissions) obj;
        if (permissiions == null) {
            if (other.permissiions != null)
                return false;
        } else if (!permissiions.equals(other.permissiions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Permissions [permissiions=" + permissiions + "]";
    }
}