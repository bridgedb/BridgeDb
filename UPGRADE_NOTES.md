Upgrade notes to BridgeDb 2.0

1. Initialing data sources

Replace

    BioDataSource.init();

with

    DataSourceTxt.init();

2. Getting DataSource's

Replace

    DataSource ds = DataSource.getBySystemCode("Ck");

with

    DataSource ds = DataSource.getExistingBySystemCode("Ck");

Similarly, and more important:

    DataSource ds = DataSource.getByFullName("PubChem-compound");

with

    DataSource ds = DataSource.getExistingByFullName("PubChem-compound");

The latter two new methods throw an IllegalArgumentException if the DataSource does not exist.
Use one of the following two methods to check first:

    DataSource.systemCodeExists("Ck");
    DataSource.fullNameExists("PubChem-compound");
