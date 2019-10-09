package tin.com.java.html.gushiwen.bean;

import java.util.List;

public class RecommentEntity {

    private List<PoetryDetailEntity> poetryDetailEntityList;
    private RecommendLinkEntity recommendHerfEntity;

    public List<PoetryDetailEntity> getPoetryDetailEntityList() {
        return poetryDetailEntityList;
    }

    public void setPoetryDetailEntityList(List<PoetryDetailEntity> poetryDetailEntityList) {
        this.poetryDetailEntityList = poetryDetailEntityList;
    }

    public RecommendLinkEntity getRecommendHerfEntity() {
        return recommendHerfEntity;
    }

    public void setRecommendHerfEntity(RecommendLinkEntity recommendHerfEntity) {
        this.recommendHerfEntity = recommendHerfEntity;
    }
}
