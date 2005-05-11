#!/bin/sh

INSTALLATION_DIR=$INSTALL_PATH
CLUSTER=thea
LAF=com.jgoodies.plaf.plastic.PlasticXPLookAndFeel
PERSISTENCE_RESOURCE_FACTORY=fr.unice.bioinfo.allonto.persistence.PersistentResourceFactory
USER_DIR=$INSTALL_PATH/.thea
DIALECT=net.sf.hibernate.dialect.MySQLDialect

sh $INSTALLATION_DIR/platform4/lib/nbexec -ui $LAF -J-DPlastic.defaultTheme=DesertBlue -J-Xmx500M -J-Dhibernate.dialect=$DIALECT -J-Dhibernate.max_fetch_depth=0 -J-Dfr.unice.bioinfo.allonto.datamodel.ResourceFactory=$PERSISTENCE_RESOURCE_FACTORY --userdir $USER_DIR --branding thea --clusters $INSTALLATION_DIR/$CLUSTER
