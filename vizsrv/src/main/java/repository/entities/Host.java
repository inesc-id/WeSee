package repository.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "host", uniqueConstraints = {@UniqueConstraint(columnNames = {"ip", "dataSourceId"})})
public class Host implements Serializable {
    @Id
    @GeneratedValue
    public long id;
    @Column(nullable = false)
    public String ip;
    @ManyToOne
    @JoinColumn(name="dataSourceId", referencedColumnName = "id")
    public DataSource dataSource;
    @NotNull
    public int isIpv4;
    public String dns;
}
