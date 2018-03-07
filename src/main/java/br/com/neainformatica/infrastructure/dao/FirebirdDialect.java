package br.com.neainformatica.infrastructure.dao;

import org.hibernate.MappingException;

public class FirebirdDialect extends org.hibernate.dialect.FirebirdDialect {

	
		
	@Override
	public boolean supportsPooledSequences() {
		return true;
	}

	@Override
	public boolean qualifyIndexName() {
		return false;
	}

	@Override
	public boolean supportsExistsInSelect() {
		return true;
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {

		referencedTable = HibernateInterceptor.removeSchema(referencedTable);

		return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
	}

	@Override
	public String getDropTableString(String tableName) {
		tableName = HibernateInterceptor.removeSchema(tableName);
		return super.getDropTableString(tableName);
	}
	
	@Override
	public String getCreateSequenceString(String sequenceName) {
		sequenceName = HibernateInterceptor.removeSchema(sequenceName); 
		return super.getCreateSequenceString(sequenceName);
	}
	
	@Override
	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
		/*if ( supportsPooledSequences() ) {
			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
		}
		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
		*/
		return getCreateSequenceString( sequenceName );
	}


}
