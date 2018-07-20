package com.dafyjk.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StepSequenceFactory {
    private static final Log LOGGER = LogFactory.getLog(StepSequenceFactory.class);
    private String tableName = "tb_sys_seq_no";
    private DataSource dataSource;
    private Boolean dateCutoff = Boolean.valueOf(false);
    private String type;
    private Integer step = Integer.valueOf(1);
    private String format;
    private String dateFormat;
    private StepSequence stepSequence;
    private Integer typeLen = Integer.valueOf(50);

    public IdCreator getSequence(){
        return this.stepSequence;
    }

    public Class<?> getObjectType() {
        return StepSequence.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void init() throws Exception {
        Assert.isTrue(this.type != null && this.type.length() <= this.typeLen.intValue(), "type is required and must length 10.");
        Assert.isTrue(this.format != null && !this.format.trim().isEmpty(), "format is required.");
        Assert.isTrue(this.format.matches(".*\\{#+\\}.*"), "format no configuration: {###}.");
        Assert.isTrue(this.tableName != null && !this.tableName.trim().isEmpty(), "tableName is required.");
        this.initTable();
        this.stepSequence = new StepSequence(this.tableName, this.dataSource, this.dateCutoff, this.type, this.step, this.format, this.dateFormat);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] idCreatorNames = beanFactory.getBeanNamesForType(IdCreator.class, false, false);
        Map<String, IdCreator> map = new HashMap();
        StringBuffer buf = new StringBuffer();
        String[] var9 = idCreatorNames;
        int var8 = idCreatorNames.length;

        for (int var7 = 0; var7 < var8; ++var7) {
            String idCreatorName = var9[var7];
            IdCreator idCreator = (IdCreator) beanFactory.getBean(idCreatorName);
            if (map.containsKey(idCreator.toString())) {
                buf.append(idCreator.toString()).append(",");
            } else {
                map.put(idCreator.toString(), idCreator);
            }
        }

        if (buf.length() > 0) {
            LOGGER.error("exist repeat " + buf.toString() + ". please double check.");
            throw new BeanCreationException("exist repeat type :" + buf.toString() + "please double check.");
        }
    }

    private void initTable() throws Exception {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedstatement = null;
        ResultSet result = null;

        try {
            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            result = connection.getMetaData().getTables((String) null, (String) null, this.tableName, (String[]) null);
            statement = connection.createStatement();
            if (result.next()) {
                LOGGER.warn(this.tableName + " already exist.");
            } else {
                String createTable = "create table " + this.tableName + "(" + "type" + " varchar(" + this.typeLen + ") not null, " + "seq_no" + " BIGINT, datetime datetime, primary key(" + "type" + "))";
                LOGGER.debug(createTable);
                statement.execute(createTable);
            }

            LOGGER.debug("check data.");
            result = statement.executeQuery("select count(*) from " + this.tableName + " where type = '" + this.type + "'");
            result.next();
            int count = result.getInt(1);
            if (count == 0) {
                preparedstatement = connection.prepareStatement("INSERT INTO " + this.tableName + " (type, seq_no, datetime) VALUES (?, ?, ?)");
                preparedstatement.setString(1, this.type);
                preparedstatement.setInt(2, 0);
                preparedstatement.setTimestamp(3, new Timestamp((new Date()).getTime()));
                preparedstatement.execute();
            }

            connection.commit();
        } catch (SQLIntegrityConstraintViolationException var11) {
            LOGGER.debug("ignore exception: constraint (primary key) has been violated. ", var11);
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLSyntaxErrorException var12) {
            LOGGER.debug("ignore exception: table '" + this.tableName + "' already exists", var12);
            if (connection != null) {
                connection.rollback();
            }
        } catch (Exception var13) {
            LOGGER.error("init table " + this.tableName + " error happen.", var13);
            if (connection != null) {
                connection.rollback();
            }

            throw new Exception("init table " + this.tableName + " error happen.", var13);
        } finally {
            if (result != null) {
                result.close();
                result = null;
            }

            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (preparedstatement != null) {
                preparedstatement.close();
                preparedstatement = null;
            }

            if (connection != null) {
                connection.close();
                connection = null;
            }

        }

    }

    public void setDateCutoff(Boolean dateCutoff) {
        this.dateCutoff = dateCutoff;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

