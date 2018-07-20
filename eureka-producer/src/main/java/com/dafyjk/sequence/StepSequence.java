package com.dafyjk.sequence;

//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by Fernflower decompiler)
//

import com.dafyjk.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepSequence implements IdCreator {
    private static final Log LOGGER = LogFactory.getLog(StepSequence.class);
    private String tableName;
    private DataSource dataSource;
    private Boolean dateCutoff;
    private String type;
    private Integer step;
    private String format;
    private String dateFormat;
    private int currentStep = 0;
    private int currentSequenceNo;
    private Date currentDate;
    private Date updateSeqNoTime;

    StepSequence(String tableName, DataSource dataSource, Boolean dateCutoff, String type, Integer step, String format, String dateFormat) throws Exception {
        this.tableName = tableName;
        this.dataSource = dataSource;
        this.dateCutoff = dateCutoff;
        this.type = type;
        this.step = step;
        this.format = format;
        this.dateFormat = dateFormat;
        this.initCurrentSequenceNo();
    }

    @Override
    public String create(String[] placeholders) {
        return this.generate(this.format, placeholders);
    }

    @Override
    public String create() {
        return this.generate(this.format, (String[]) null);
    }

    @Override
    public String toString() {
        return "table:" + this.tableName + ", type:" + this.type;
    }

    private void initCurrentSequenceNo() throws Exception {
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;

        try {
            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            result = statement.executeQuery("select seq_no from " + this.tableName + " where type = '" + this.type + "' for update");
            result.next();
            this.currentSequenceNo = result.getInt(1);
            connection.commit();
        } catch (Exception var8) {
            LOGGER.error("initCurrentSequenceNo error happen.", var8);
            if (connection != null) {
                connection.rollback();
            }

            throw new Exception("initCurrentSequenceNo error happen.", var8);
        } finally {
            if (result != null) {
                result.close();
                result = null;
            }

            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (connection != null) {
                connection.close();
                connection = null;
            }

        }

    }

    private String generate(String fmt, String[] placeholders) {
        try {
            Integer curtSeqNo = this.holdCurrentStep();
            String sequence = this.generateSequence(curtSeqNo, fmt, placeholders);
            return sequence;
        } catch (Exception var5) {
            LOGGER.error("create id error happen.");
            throw new RuntimeException("create id error happen.", var5);
        }
    }

    private Pattern pattern = Pattern.compile("\\{#+\\}");
    private String generateSequence(Integer curtSeqNo, String fmt, String[] placeholders) {
        if (placeholders != null) {
            fmt = this.replaceVariable("\\{\\?\\}", fmt, placeholders);
        }

        if (this.dateFormat != null && !this.dateFormat.trim().isEmpty()) {
            fmt = this.replaceVariable("\\{date\\}", fmt, new String[]{DateUtil.format(this.updateSeqNoTime, this.dateFormat)});
        }

//        Pattern pattern = Pattern.compile("\\{#+\\}");
        Matcher matcher = pattern.matcher(fmt);
        StringBuffer buf = new StringBuffer();
        int start = 0;
        int end = 0;
        if (matcher.find()) {
            buf.append(fmt.substring(0, matcher.start()));
            start = matcher.start();
            end = matcher.end();
        }

        int numLenth = end - start - 2;
        Assert.isTrue(curtSeqNo.toString().length() <= numLenth, "The length of current seqNo is longer than that of number format.");
        StringBuffer numFormat = new StringBuffer();

        for (int i = 0; i < numLenth; ++i) {
            numFormat.append("0");
        }

        buf.append((new DecimalFormat(numFormat.toString())).format(curtSeqNo));
        buf.append(fmt.substring(end));
        return buf.toString();
    }

    private String replaceVariable(String regex, String text, String... placeholders) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuffer buf = new StringBuffer();
        int start = 0;

        for (int i = 0; matcher.find(); ++i) {
            buf.append(text.substring(start, matcher.start()));
            buf.append(placeholders[i]);
            start = matcher.end();
        }

        buf.append(text.substring(start));
        return buf.toString();
    }

    private synchronized Integer holdCurrentStep() {
        try {
            this.currentDate = new Date();
            if (this.currentStep != this.step.intValue() && this.currentStep != 0) {
                if (this.currentStep > this.step.intValue()) {
                    LOGGER.error("create sequence error happen: currentStep gt step, currentStep: " + this.currentStep);
                    this.resetStep();
                } else if (this.dateCutoff.booleanValue() && DateUtil.diffDay(this.currentDate, this.updateSeqNoTime) > 0) {
                    this.resetStep();
                } else {
                    ++this.currentSequenceNo;
                    ++this.currentStep;
                }
            } else {
                this.resetStep();
            }

            return Integer.valueOf(this.currentSequenceNo);
        } catch (Exception var2) {
            LOGGER.error("create id error happen.");
            throw new RuntimeException("create id error happen.", var2);
        }
    }

    private void resetStep() throws Exception {
        this.updateSeqNo();
        this.currentStep = 1;
    }

    private void updateSeqNo() throws Exception {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedstatement = null;
        ResultSet result = null;

        try {
            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            result = statement.executeQuery("select seq_no, datetime from " + this.tableName + " where type = '" + this.type + "' for update");
            result.next();
            preparedstatement = connection.prepareStatement("UPDATE " + this.tableName + " SET seq_no=?, datetime=? WHERE type=?");
            this.updateSeqNoTime = new Date(result.getDate(2).getTime());
            if (this.dateCutoff.booleanValue() && DateUtil.diffDay(this.currentDate, this.updateSeqNoTime) > 0) {
                preparedstatement.setInt(1, 0);
                preparedstatement.setTimestamp(2, new Timestamp(this.currentDate.getTime()));
                preparedstatement.setString(3, this.type);
                preparedstatement.executeUpdate();
                connection.commit();
                this.updateSeqNo();
                return;
            }

            this.currentSequenceNo = result.getInt(1);
            preparedstatement.setInt(1, this.currentSequenceNo + this.step.intValue());
            if (this.dateCutoff.booleanValue()) {
                if (DateUtil.diffDay(this.currentDate, this.updateSeqNoTime) > 0) {
                    preparedstatement.setTimestamp(2, new Timestamp(this.currentDate.getTime()));
                } else {
                    preparedstatement.setTimestamp(2, new Timestamp(this.updateSeqNoTime.getTime()));
                }
            } else {
                preparedstatement.setTimestamp(2, new Timestamp(this.currentDate.getTime()));
            }

            preparedstatement.setString(3, this.type);
            preparedstatement.executeUpdate();
            connection.commit();
            ++this.currentSequenceNo;
        } catch (Exception var9) {
            LOGGER.error("holdStpNoAndUpdate " + this.tableName + " error happen.", var9);
            if (connection != null) {
                connection.rollback();
            }

            throw new Exception("holdStpNoAndUpdate " + this.tableName + " error happen.", var9);
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
}

