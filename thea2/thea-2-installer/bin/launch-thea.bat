set INSTALLATION_DIR=$INSTALL_PATH
set CLUSTER=thea
set LAF=com.jgoodies.plaf.plastic.PlasticXPLookAndFeel
set PERSISTENCE_RESOURCE_FACTORY=fr.unice.bioinfo.allonto.persistence.PersistentResourceFactory
set USER_DIR=$INSTALL_PATH\.thea
set DIALECT=net.sf.hibernate.dialect.MySQLDialect

%INSTALLATION_DIR%\platform4\lib\nbexec.exe -ui %LAF% -J-DPlastic.defaultTheme=DesertBlue -J-Xmx500M -J-Dhibernate.dialect=%DIALECT% -J-Dhibernate.max_fetch_depth=0 -J-Dfr.unice.bioinfo.allonto.datamodel.ResourceFactory=%PERSISTENCE_RESOURCE_FACTORY% --userdir %USER_DIR% --branding thea --clusters %INSTALLATION_DIR%\%CLUSTER%