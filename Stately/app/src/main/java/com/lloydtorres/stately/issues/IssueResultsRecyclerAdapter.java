/**
 * Copyright 2016 Lloyd Torres
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lloydtorres.stately.issues;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydtorres.stately.R;
import com.lloydtorres.stately.census.TrendsActivity;
import com.lloydtorres.stately.dto.CensusDelta;
import com.lloydtorres.stately.dto.IssuePostcard;
import com.lloydtorres.stately.dto.IssueResult;
import com.lloydtorres.stately.dto.IssueResultHeadline;
import com.lloydtorres.stately.dto.Nation;
import com.lloydtorres.stately.helpers.PinkaHelper;
import com.lloydtorres.stately.helpers.RaraHelper;
import com.lloydtorres.stately.helpers.SparkleHelper;
import com.lloydtorres.stately.helpers.network.DashHelper;

import org.atteo.evo.inflector.English;

import java.util.List;
import java.util.Locale;

/**
 * Created by Lloyd on 2016-02-29.
 * An adapter for showing the results of an issue resolution.
 */
public class IssueResultsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String[] WORLD_CENSUS_ITEMS;

    private static final int ISSUE_RESULT_CARD = 0;
    private static final int HEADLINE_CARD = 1;
    private static final int POSTCARD_CARD = 2;
    private static final int CENSUSDELTA_CARD = 3;

    private Context context;
    private List<Object> content;
    private Nation mNation;

    public IssueResultsRecyclerAdapter(Context c, List<Object> con, Nation n) {
        context = c;
        WORLD_CENSUS_ITEMS = context.getResources().getStringArray(R.array.census);
        setContent(con, n);
    }

    public void setContent(List<Object> con, Nation n) {
        content = con;
        mNation = n;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ISSUE_RESULT_CARD:
                View issueResultCard = inflater.inflate(R.layout.card_issue_result, parent, false);
                viewHolder = new IssueResultCard(issueResultCard);
                break;
            case POSTCARD_CARD:
                View postcardCard = inflater.inflate(R.layout.card_postcard, parent, false);
                viewHolder = new PostcardCard(postcardCard);
                break;
            case CENSUSDELTA_CARD:
                View censusDeltaCard = inflater.inflate(R.layout.card_census_delta, parent, false);
                viewHolder = new CensusDeltaCard(censusDeltaCard);
                break;
            default:
                View headlineCard = inflater.inflate(R.layout.card_headline, parent, false);
                viewHolder = new HeadlineCard(headlineCard);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ISSUE_RESULT_CARD:
                IssueResultCard issueResultCard = (IssueResultCard) holder;
                issueResultCard.init((IssueResult) content.get(position));
                break;
            case POSTCARD_CARD:
                PostcardCard postcardCard = (PostcardCard) holder;
                postcardCard.init((IssuePostcard) content.get(position));
                break;
            case CENSUSDELTA_CARD:
                CensusDeltaCard censusDeltaCard = (CensusDeltaCard) holder;
                censusDeltaCard.init((CensusDelta) content.get(position));
                break;
            default:
                HeadlineCard headlineCard = (HeadlineCard) holder;
                headlineCard.init((IssueResultHeadline) content.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (content.get(position) instanceof IssueResult) {
            return ISSUE_RESULT_CARD;
        }
        else if (content.get(position) instanceof IssueResultHeadline) {
            return HEADLINE_CARD;
        }
        else if (content.get(position) instanceof IssuePostcard) {
            return POSTCARD_CARD;
        }
        else if (content.get(position) instanceof CensusDelta) {
            return CENSUSDELTA_CARD;
        }
        return -1;
    }

    public static void setIssueResultsFormatting(Context c, TextView t, Nation nationData, String target) {
        if (nationData != null && target != null) {
            target = target.replace("@@NAME@@", nationData.name);
            target = target.replace("@@$nation->query(\"name\")@@", nationData.name);
            target = target.replace("@@uc($nation->query(\"name\"))@@", nationData.name.toUpperCase(Locale.US));
            target = target.replace("@@REGION@@", nationData.region);
            target = target.replace("@@MAJORINDUSTRY@@", nationData.industry);
            target = target.replace("@@POPULATION@@", SparkleHelper.getPrettifiedNumber(nationData.popBase));
            target = target.replace("@@TYPE@@", nationData.prename);
            target = target.replace("@@ANIMAL@@", nationData.animal);
            target = target.replace("@@ucfirst(ANIMAL)@@", SparkleHelper.toNormalCase(nationData.animal));
            target = target.replace("@@PL(ANIMAL)@@", English.plural(nationData.animal));
            target = target.replace("@@ucfirst(PL(ANIMAL))@@", SparkleHelper.toNormalCase(English.plural(nationData.animal)));
            target = target.replace("@@CURRENCY@@", nationData.currency);
            target = target.replace("@@PL(CURRENCY)@@", SparkleHelper.getCurrencyPlural(nationData.currency));
            target = target.replace("@@ucfirst(PL(CURRENCY))@@", SparkleHelper.toNormalCase(SparkleHelper.getCurrencyPlural(nationData.currency)));
            target = target.replace("@@SLOGAN@@", nationData.motto);
            target = target.replace("@@DEMONYM@@", nationData.demAdjective);
            target = target.replace("@@DEMONYM2@@", nationData.demNoun);
            target = target.replace("@@PL(DEMONYM2)@@", nationData.demPlural);

            String valCapital = String.format(Locale.US, c.getString(R.string.issue_capital_none), nationData.name);
            if (nationData.capital != null) {
                valCapital = nationData.capital;
            }
            target = target.replace("@@CAPITAL@@", valCapital);
            target = target.replace("@@$nation->query_capital()@@", valCapital);

            String valLeader = c.getString(R.string.issue_leader_none);
            if (nationData.leader != null) {
                valLeader = nationData.leader;
            }
            target = target.replace("@@LEADER@@", valLeader);
            target = target.replace("@@$nation->query_leader()@@", valLeader);

            String valReligion = c.getString(R.string.issue_religion_none);
            if (nationData.religion != null) {
                valReligion = nationData.religion;
            }
            target = target.replace("@@FAITH@@", valReligion);
            target = target.replace("@@$nation->query_faith()@@", valReligion);
        }

        t.setText(SparkleHelper.getHtmlFormatting(target).toString());
    }

    public class IssueResultCard extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView mainResult;
        private TextView reclassResult;
        private TextView issueContent;
        private TextView expander;

        public IssueResultCard(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.card_issue_result_image);
            mainResult = (TextView) v.findViewById(R.id.card_issue_result_main_result);
            reclassResult = (TextView) v.findViewById(R.id.card_issue_result_reclass_result);
            issueContent = (TextView) v.findViewById(R.id.card_issue_result_issue_content);
            expander = (TextView) v.findViewById(R.id.card_issue_result_expand);
        }

        public void init(IssueResult result) {
            if (result.image != null) {
                image.setVisibility(View.VISIBLE);
                DashHelper dashie = DashHelper.getInstance(context);
                dashie.loadImage(RaraHelper.getBannerURL(result.image), image, false);
            } else {
                image.setVisibility(View.GONE);
            }

            setIssueResultsFormatting(context, mainResult, mNation, result.mainResult);
            if (result.reclassResults != null) {
                reclassResult.setVisibility(View.VISIBLE);
                setIssueResultsFormatting(context, reclassResult, mNation, result.reclassResults);
            } else {
                reclassResult.setVisibility(View.GONE);
            }

            StringBuilder sb = new StringBuilder(result.issueContent);
            sb.append("<br><br>");
            sb.append(result.issuePosition);
            issueContent.setText(SparkleHelper.getHtmlFormatting(sb.toString()).toString());

            expander.setText(context.getString(R.string.issue_read_more));
            expander.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueContent.setVisibility(issueContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    expander.setText(context.getString(issueContent.getVisibility() == View.VISIBLE ? R.string.issue_show_less : R.string.issue_read_more));
                }
            });
        }
    }

    public class HeadlineCard extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;

        public HeadlineCard(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.card_issue_headline);
            img = (ImageView) v.findViewById(R.id.card_issue_headline_img);
        }

        public void init(IssueResultHeadline headline) {
            headline.headline = headline.headline.trim();
            setIssueResultsFormatting(context, title, mNation, headline.headline);
            DashHelper.getInstance(context).loadImage(headline.imgUrl, img, false);
        }
    }

    public class PostcardCard extends RecyclerView.ViewHolder {
        private TextView nationName;
        private TextView postContent;
        private ImageView img;

        public PostcardCard(View v) {
            super(v);
            nationName = (TextView) v.findViewById(R.id.card_postcard_nation) ;
            postContent = (TextView) v.findViewById(R.id.card_postcard_title);
            img = (ImageView) v.findViewById(R.id.card_postcard_img);
        }

        public void init(IssuePostcard card) {
            nationName.setText(mNation.name);
            setIssueResultsFormatting(context, postContent, mNation, card.title.trim());
            DashHelper.getInstance(context).loadImage(card.imgUrl, img, false);
        }
    }

    public class CensusDeltaCard extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CensusDelta delta;

        private CardView cardHolder;
        private TextView title;
        private TextView unit;
        private ImageView trend;
        private TextView value;

        public CensusDeltaCard(View v) {
            super(v);
            cardHolder = (CardView) v.findViewById(R.id.card_census_delta_main);
            title = (TextView) v.findViewById(R.id.card_delta_name);
            unit = (TextView) v.findViewById(R.id.card_delta_unit);
            trend = (ImageView) v.findViewById(R.id.card_delta_trend);
            trend.setVisibility(View.VISIBLE);
            value = (TextView) v.findViewById(R.id.card_delta_value);
            v.setOnClickListener(this);
        }

        public void init(CensusDelta d) {
            delta = d;
            cardHolder.setCardBackgroundColor(ContextCompat.getColor(context, delta.isPositive ? R.color.colorFreedom14 : R.color.colorFreedom0));

            String[] censusType = SparkleHelper.getCensusScale(WORLD_CENSUS_ITEMS, delta.censusId);
            title.setText(censusType[0]);
            unit.setText(censusType[1]);
            trend.setImageResource(delta.isPositive ? R.drawable.ic_trend_up : R.drawable.ic_trend_down);
            value.setText(delta.delta);
        }

        @Override
        public void onClick(View v) {
            String userId = PinkaHelper.getActiveUser(context).nationId;
            SparkleHelper.startTrends(context, userId, TrendsActivity.TREND_NATION, delta.censusId);
        }
    }
}
