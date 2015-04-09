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
