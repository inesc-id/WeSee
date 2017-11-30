package repository.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="connection_models",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"sourceHostId", "destinationHostId", "dataSourceId"})})
public class Connection implements Serializable {
    @Id
    @GeneratedValue
    public int id;
    @ManyToOne
    @JoinColumn(name="sourceHostId", referencedColumnName = "id")
    public Host sourceHost;
    @ManyToOne
    @JoinColumn(name="destinationHostId", referencedColumnName = "id")
    public Host destinationHost;
    @ManyToOne
    @JoinColumn(name="dataSourceId", referencedColumnName = "id")
    public DataSource dataSource;
    @OneToMany(mappedBy = "connection")
    public Set<ConnectionOccurrence> connectionOccurrences;
}
