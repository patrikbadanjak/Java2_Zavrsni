package hr.algebra.dal.repository;

import hr.algebra.dal.sql.SqlRepository;

public class RepositoryFactory {

    private RepositoryFactory() {}

    public Repository getRepository() {
        return new SqlRepository();
    }
}
