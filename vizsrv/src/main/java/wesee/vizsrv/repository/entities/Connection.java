package wesee.vizsrv.repository.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"sourceHostId", "destinationHostId", "dataSourceId"})})
public class Connection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @ManyToOne(fetch = FetchType.EAGER,optional=false)
    @JoinColumn(name="sourceHostId")
    public Host sourceHost;
    @ManyToOne(fetch = FetchType.EAGER,optional=false)
    @JoinColumn(name="destinationHostId")
    public Host destinationHost;
    @ManyToOne(fetch = FetchType.LAZY,optional=false)
    @JoinColumn(name="dataSourceId")
    public DataSource dataSource;
    @OneToMany(mappedBy = "connection", fetch = FetchType.LAZY)
    public List<ConnectionOccurrence> connectionOccurrences = new ArrayList<>();
}
