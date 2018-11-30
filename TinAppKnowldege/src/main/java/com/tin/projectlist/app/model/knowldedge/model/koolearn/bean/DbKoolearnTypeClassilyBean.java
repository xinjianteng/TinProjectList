package com.tin.projectlist.app.model.knowldedge.model.koolearn.bean;

public class DbKoolearnTypeClassilyBean {
    private String typeClassilyName;
    private String typeClassilyHerf;


    public String getTypeClassilyName() {
        return typeClassilyName;
    }

    public void setTypeClassilyName(String typeClassilyName) {
        this.typeClassilyName = typeClassilyName;
    }

    public String getTypeClassilyHerf() {
        return typeClassilyHerf;
    }

    public void setTypeClassilyHerf(String typeClassilyHerf) {
        this.typeClassilyHerf = typeClassilyHerf;
    }

    @Override
    public String toString() {
        return "TypeClassilyBean{" +
                "typeClassilyName='" + typeClassilyName + '\'' +
                ", typeClassilyHerf='" + typeClassilyHerf + '\'' +
                '}';
    }
}
