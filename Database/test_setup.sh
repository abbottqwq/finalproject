/etc/init.d/postgresql start
psql -f createTestTable.sql
/etc/init.d/postgresql stop
