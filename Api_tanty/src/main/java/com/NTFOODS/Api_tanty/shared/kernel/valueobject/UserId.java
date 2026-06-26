package com.NTFOODS.Api_tanty.shared.kernel.valueobject;

import java.util.Objects;

public final class UserId {
    private final String matricule;
    public UserId(String matricule){
        if(matricule== null || matricule.isBlank()){
            throw new IllegalArgumentException("Le matricule ne peut pas être vide");
        }
       this.matricule=matricule;
    }

    public String getMatricule(){
        return matricule;
    }

    @Override
    public  boolean equals(Object o){
        if (this == o) return  true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId= (UserId) o;
        return Objects.equals(matricule, userId.matricule);
    }
    @Override
    public int hashCode(){
        return Objects.hash(matricule);
    }

    @Override
    public String toString(){
        return matricule;
    }
}
