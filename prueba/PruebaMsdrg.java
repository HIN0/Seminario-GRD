// Prueba minima del agrupador MS-DRG v44 (CMS)
// Basado en el ejemplo de la guia "MS-DRG Software Java API guide v44.0".
// Nota: en el ejemplo oficial del PDF, la declaracion de la lista de
// diagnosticos secundarios quedo dentro de un comentario y no compila.
// Aqui esta corregido.

import com.mmm.his.cer.foundation.exception.FoundationException;
import com.mmm.his.cer.foundation.model.GfcPoa;
import gov.agency.msdrg.model.v2.MsdrgOption;
import gov.agency.msdrg.model.v2.MsdrgRuntimeOption;
import gov.agency.msdrg.model.v2.RuntimeOptions;
import gov.agency.msdrg.model.v2.enumeration.MarkingLogicTieBreaker;
import gov.agency.msdrg.model.v2.enumeration.MsdrgAffectDrgOptionFlag;
import gov.agency.msdrg.model.v2.enumeration.MsdrgDischargeStatus;
import gov.agency.msdrg.model.v2.enumeration.MsdrgHospitalStatusOptionFlag;
import gov.agency.msdrg.model.v2.enumeration.MsdrgSex;
import gov.agency.msdrg.model.v2.transfer.MsdrgClaim;
import gov.agency.msdrg.model.v2.transfer.input.MsdrgInput;
import gov.agency.msdrg.model.v2.transfer.input.MsdrgInputDxCode;
import gov.agency.msdrg.model.v2.transfer.input.MsdrgInputPrCode;
import gov.agency.msdrg.model.v2.transfer.output.MsdrgOutputData;
import gov.agency.msdrg.v440.MsdrgComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PruebaMsdrg {

    public static void main(String[] args) throws FoundationException {
        // 1. Opciones del agrupador (igual que el ejemplo de CMS)
        MsdrgRuntimeOption runtimeOptions = new MsdrgRuntimeOption();
        RuntimeOptions options = new RuntimeOptions();
        options.setPoaReportingExempt(MsdrgHospitalStatusOptionFlag.NON_EXEMPT);
        options.setComputeAffectDrg(MsdrgAffectDrgOptionFlag.COMPUTE);
        options.setMarkingLogicTieBreaker(MarkingLogicTieBreaker.CLINICAL_SIGNIFICANCE);
        runtimeOptions.put(MsdrgOption.RUNTIME_OPTION_FLAGS, options);

        // El componente se crea UNA sola vez y se reutiliza (lo recomienda la guia)
        MsdrgComponent component = new MsdrgComponent(runtimeOptions);

        // ---- CASO 1: el ejemplo oficial de CMS (sirve para verificar la instalacion)
        List<MsdrgInputPrCode> proc1 = new ArrayList<>();
        proc1.add(new MsdrgInputPrCode("0TY00Z0"));
        proc1.add(new MsdrgInputPrCode("0FYG0Z0"));
        proc1.add(new MsdrgInputPrCode("5A1D70Z"));
        List<MsdrgInputDxCode> sdx1 = new ArrayList<>();
        sdx1.add(new MsdrgInputDxCode("E0800", GfcPoa.Y));

        MsdrgInput caso1 = MsdrgInput.builder()
                .withPrincipalDiagnosisCode(new MsdrgInputDxCode("I120", GfcPoa.Y))
                .withAdmissionDiagnosisCode(new MsdrgInputDxCode("I120", GfcPoa.Y))
                .withSecondaryDiagnosisCodes(sdx1)
                .withProcedureCodes(proc1)
                .withAgeInYears(0)
                .withAgeDaysAdmit(0)
                .withAgeDaysDischarge(0)
                .withSex(MsdrgSex.MALE)
                .withDischargeStatus(MsdrgDischargeStatus.HOME_SELFCARE_ROUTINE)
                .build();
        agrupar("Caso 1 - ejemplo oficial CMS", caso1, component);

        // ---- CASO 2: oncologico quirurgico inventado
        // C50911 = cancer de mama derecha (mujer), sitio no especificado
        // 0HTT0ZZ = reseccion de mama derecha, abordaje abierto
        List<MsdrgInputPrCode> proc2 = new ArrayList<>();
        proc2.add(new MsdrgInputPrCode("0HTT0ZZ"));

        MsdrgInput caso2 = MsdrgInput.builder()
                .withPrincipalDiagnosisCode(new MsdrgInputDxCode("C50911", GfcPoa.Y))
                .withSecondaryDiagnosisCodes(new ArrayList<>())
                .withProcedureCodes(proc2)
                .withAgeInYears(55)
                .withSex(MsdrgSex.FEMALE)
                .withDischargeStatus(MsdrgDischargeStatus.HOME_SELFCARE_ROUTINE)
                .build();
        agrupar("Caso 2 - cancer de mama, mastectomia", caso2, component);

        // ---- CASO 3: codigo CIE-10 OMS sin traducir (4 caracteres)
        // C509 existe en la CIE-10 de la OMS, pero en ICD-10-CM le falta especificidad.
        // Esperamos ver como reacciona el agrupador.
        MsdrgInput caso3 = MsdrgInput.builder()
                .withPrincipalDiagnosisCode(new MsdrgInputDxCode("C509", GfcPoa.Y))
                .withSecondaryDiagnosisCodes(new ArrayList<>())
                .withProcedureCodes(proc2)
                .withAgeInYears(55)
                .withSex(MsdrgSex.FEMALE)
                .withDischargeStatus(MsdrgDischargeStatus.HOME_SELFCARE_ROUTINE)
                .build();
        agrupar("Caso 3 - codigo OMS sin traducir", caso3, component);
    }

    private static void agrupar(String nombre, MsdrgInput input, MsdrgComponent component)
            throws FoundationException {
        MsdrgClaim claim = new MsdrgClaim(input);
        component.process(claim);
        Optional<MsdrgOutputData> salida = claim.getOutput();

        System.out.println("==== " + nombre + " ====");
        if (salida.isEmpty()) {
            System.out.println("Sin salida del agrupador");
            return;
        }
        MsdrgOutputData out = salida.get();
        System.out.println("Codigo de retorno : " + out.getFinalGrc());
        System.out.println("MDC final         : " + out.getFinalMdc());
        System.out.println("MS-DRG final      : " + out.getFinalDrg());
        System.out.println("Tipo med/quir     : " + out.getFinalMedSurgType());
        System.out.println("Severidad         : " + out.getFinalSeverity());
        System.out.println("PDx reconocido    : "
                + out.getPdxOutput().isDiagnosisRecognizedByGrouper());
        System.out.println();
    }
}
