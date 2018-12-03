package tin.com.java.html.gushiwen.bean;

import java.util.List;

public class RecommentEntity {

    private List<PoetryDetailEntity> poetryDetailEntityList;
    private RecommendHerfEntity recommendHerfEntity;

    public List<PoetryDetailEntity> getPoetryDetailEntityList() {
        return poetryDetailEntityList;
    }

    public void setPoetryDetailEntityList(List<PoetryDetailEntity> poetryDetailEntityList) {
        this.poetryDetailEntityList = poetryDetailEntityList;
    }

    public RecommendHerfEntity getRecommendHerfEntity() {
        return recommendHerfEntity;
    }

    public void setRecommendHerfEntity(RecommendHerfEntity recommendHerfEntity) {
        this.recommendHerfEntity = recommendHerfEntity;
    }

    @Override
    public String toString() {
        return "RecommentEntity{" +
                "poetryDetailEntityList=" + poetryDetailEntityList +
                ", recommendHerfEntity=" + recommendHerfEntity +
                '}';
    }
}
