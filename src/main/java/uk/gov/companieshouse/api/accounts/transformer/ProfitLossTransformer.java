package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.GrossProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.OperatingProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossBeforeTaxEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossForFinancialYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossBeforeTax;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossForFinancialYear;

@Component
public class ProfitLossTransformer implements GenericTransformer<ProfitLoss, ProfitLossEntity> {

    @Override
    public ProfitLossEntity transform(ProfitLoss rest) {

        ProfitLossDataEntity profitLossDataEntity = new ProfitLossDataEntity();

        if (rest.getGrossProfitOrLoss() != null) {
            GrossProfitOrLossEntity grossProfitOrLoss = new GrossProfitOrLossEntity();
            BeanUtils.copyProperties(rest.getGrossProfitOrLoss(), grossProfitOrLoss);
            profitLossDataEntity.setGrossProfitOrLoss(grossProfitOrLoss);
        }

        if (rest.getOperatingProfitOrLoss() != null) {
            OperatingProfitOrLossEntity operatingProfitOrLoss = new OperatingProfitOrLossEntity();
            BeanUtils.copyProperties(rest.getOperatingProfitOrLoss(), operatingProfitOrLoss);
            profitLossDataEntity.setOperatingProfitOrLoss(operatingProfitOrLoss);
        }

        if (rest.getProfitOrLossBeforeTax() != null) {
            ProfitOrLossBeforeTaxEntity profitOrLossBeforeTax = new ProfitOrLossBeforeTaxEntity();
            BeanUtils.copyProperties(rest.getProfitOrLossBeforeTax(), profitOrLossBeforeTax);
            profitLossDataEntity.setProfitOrLossBeforeTax(profitOrLossBeforeTax);
        }

        if (rest.getProfitOrLossForFinancialYear() != null) {
            ProfitOrLossForFinancialYearEntity profitOrLossForFinancialYear = new ProfitOrLossForFinancialYearEntity();
            BeanUtils.copyProperties(rest.getProfitOrLossForFinancialYear(), profitOrLossForFinancialYear);
            profitLossDataEntity.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);
        }

        ProfitLossEntity profitLossEntity = new ProfitLossEntity();
        profitLossEntity.setData(profitLossDataEntity);
        return profitLossEntity;
    }

    @Override
    public ProfitLoss transform(ProfitLossEntity entity) {

        ProfitLoss profitLoss = new ProfitLoss();

        ProfitLossDataEntity profitLossDataEntity = entity.getData();

        if (profitLossDataEntity.getGrossProfitOrLoss() != null) {
            GrossProfitOrLoss grossProfitOrLoss = new GrossProfitOrLoss();
            BeanUtils.copyProperties(profitLossDataEntity.getGrossProfitOrLoss(), grossProfitOrLoss);
            profitLoss.setGrossProfitOrLoss(grossProfitOrLoss);
        }

        if (profitLossDataEntity.getOperatingProfitOrLoss() != null) {
            OperatingProfitOrLoss operatingProfitOrLoss = new OperatingProfitOrLoss();
            BeanUtils.copyProperties(profitLossDataEntity.getOperatingProfitOrLoss(), operatingProfitOrLoss);
            profitLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);
        }

        if (profitLossDataEntity.getProfitOrLossBeforeTax() != null) {
            ProfitOrLossBeforeTax profitOrLossBeforeTax = new ProfitOrLossBeforeTax();
            BeanUtils.copyProperties(profitLossDataEntity.getProfitOrLossBeforeTax(), profitOrLossBeforeTax);
            profitLoss.setProfitOrLossBeforeTax(profitOrLossBeforeTax);
        }

        if (profitLossDataEntity.getProfitOrLossForFinancialYear() != null) {
            ProfitOrLossForFinancialYear profitOrLossForFinancialYear = new ProfitOrLossForFinancialYear();
            BeanUtils.copyProperties(profitLossDataEntity.getProfitOrLossForFinancialYear(), profitOrLossForFinancialYear);
            profitLoss.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);
        }

        return profitLoss;
    }
}
