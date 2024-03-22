package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.GrossProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.OperatingProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossBeforeTaxEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossForFinancialYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossBeforeTax;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossForFinancialYear;

@Component
public class ProfitAndLossTransformer implements GenericTransformer<ProfitAndLoss, ProfitAndLossEntity> {

    @Override
    public ProfitAndLossEntity transform(ProfitAndLoss rest) {
        ProfitAndLossDataEntity profitAndLossDataEntity = new ProfitAndLossDataEntity();
        BeanUtils.copyProperties(rest, profitAndLossDataEntity);

        if (rest.getGrossProfitOrLoss() != null) {
            GrossProfitOrLossEntity grossProfitOrLoss = new GrossProfitOrLossEntity();
            BeanUtils.copyProperties(rest.getGrossProfitOrLoss(), grossProfitOrLoss);
            profitAndLossDataEntity.setGrossProfitOrLoss(grossProfitOrLoss);
        }

        if (rest.getOperatingProfitOrLoss() != null) {
            OperatingProfitOrLossEntity operatingProfitOrLoss = new OperatingProfitOrLossEntity();
            BeanUtils.copyProperties(rest.getOperatingProfitOrLoss(), operatingProfitOrLoss);
            profitAndLossDataEntity.setOperatingProfitOrLoss(operatingProfitOrLoss);
        }

        if (rest.getProfitOrLossBeforeTax() != null) {
            ProfitOrLossBeforeTaxEntity profitOrLossBeforeTax = new ProfitOrLossBeforeTaxEntity();
            BeanUtils.copyProperties(rest.getProfitOrLossBeforeTax(), profitOrLossBeforeTax);
            profitAndLossDataEntity.setProfitOrLossBeforeTax(profitOrLossBeforeTax);
        }

        if (rest.getProfitOrLossForFinancialYear() != null) {
            ProfitOrLossForFinancialYearEntity profitOrLossForFinancialYear = new ProfitOrLossForFinancialYearEntity();
            BeanUtils.copyProperties(rest.getProfitOrLossForFinancialYear(), profitOrLossForFinancialYear);
            profitAndLossDataEntity.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);
        }

        ProfitAndLossEntity profitAndLossEntity = new ProfitAndLossEntity();
        profitAndLossEntity.setData(profitAndLossDataEntity);
        return profitAndLossEntity;
    }

    @Override
    public ProfitAndLoss transform(ProfitAndLossEntity entity) {
        ProfitAndLoss profitAndLoss = new ProfitAndLoss();

        ProfitAndLossDataEntity profitAndLossDataEntity = entity.getData();
        BeanUtils.copyProperties(profitAndLossDataEntity, profitAndLoss);

        if (profitAndLossDataEntity.getGrossProfitOrLoss() != null) {
            GrossProfitOrLoss grossProfitOrLoss = new GrossProfitOrLoss();
            BeanUtils.copyProperties(profitAndLossDataEntity.getGrossProfitOrLoss(), grossProfitOrLoss);
            profitAndLoss.setGrossProfitOrLoss(grossProfitOrLoss);
        }

        if (profitAndLossDataEntity.getOperatingProfitOrLoss() != null) {
            OperatingProfitOrLoss operatingProfitOrLoss = new OperatingProfitOrLoss();
            BeanUtils.copyProperties(profitAndLossDataEntity.getOperatingProfitOrLoss(), operatingProfitOrLoss);
            profitAndLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);
        }

        if (profitAndLossDataEntity.getProfitOrLossBeforeTax() != null) {
            ProfitOrLossBeforeTax profitOrLossBeforeTax = new ProfitOrLossBeforeTax();
            BeanUtils.copyProperties(profitAndLossDataEntity.getProfitOrLossBeforeTax(), profitOrLossBeforeTax);
            profitAndLoss.setProfitOrLossBeforeTax(profitOrLossBeforeTax);
        }

        if (profitAndLossDataEntity.getProfitOrLossForFinancialYear() != null) {
            ProfitOrLossForFinancialYear profitOrLossForFinancialYear = new ProfitOrLossForFinancialYear();
            BeanUtils.copyProperties(profitAndLossDataEntity.getProfitOrLossForFinancialYear(), profitOrLossForFinancialYear);
            profitAndLoss.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);
        }

        return profitAndLoss;
    }
}
