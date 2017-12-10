package wesee.vizsrv.graph.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wesee.vizsrv.repository.HibernateLoader;
import wesee.vizsrv.graph.models.DataSource;
import wesee.vizsrv.repository.repository.DataSourceRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class DataSourcesLoader {
    @Autowired
    private DataSourceRepository dataSourceRepository;
    public static DataSource fromDbDataSource(wesee.vizsrv.repository.entities.DataSource dbDataSource)
    {
        DataSource dataSource = new DataSource();
        dataSource.id = dbDataSource.id;
        dataSource.name = dbDataSource.name;
        return dataSource;
    }

    public DataSource[] loadDataSources() {
        HibernateLoader hibernateLoader = new HibernateLoader(dataSourceRepository,
                null, null);
        wesee.vizsrv.repository.entities.DataSource[] dbDataSources = hibernateLoader.getDataSources();
        DataSource[] entityDataSources = new DataSource[dbDataSources.length];
        Arrays.stream(dbDataSources).map(dataSource -> fromDbDataSource(dataSource))
                .collect(Collectors.toList())
                .toArray(entityDataSources);
        return entityDataSources;
    }
}
