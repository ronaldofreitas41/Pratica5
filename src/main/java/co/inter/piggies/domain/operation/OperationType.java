package co.inter.piggies.domain.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationType {

    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW");


    private String description;

}
