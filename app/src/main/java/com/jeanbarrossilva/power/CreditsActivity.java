package com.jeanbarrossilva.power;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;

public class CreditsActivity extends SettingsActivity {
  ConstraintLayout creditAndreLuizSilva;
  ConstraintLayout creditJeanSilva;
  ConstraintLayout creditAllanDePaula;
  ConstraintLayout creditJoaoPedroRocha;
  ConstraintLayout creditJoaoVitorAraujo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings_credits);

    creditAndreLuizSilva = findViewById(R.id.credit_andre_luiz_silva);
    creditJeanSilva = findViewById(R.id.credit_jean_silva);
    creditAllanDePaula = findViewById(R.id.credit_allan_de_paula);
    creditJoaoPedroRocha = findViewById(R.id.credit_joao_pedro_rocha);
    creditJoaoVitorAraujo = findViewById(R.id.credit_joao_vitor_araujo);

    if (Build.VERSION.SDK_INT >= 21) {
      if (preferences.getBoolean("isNight", false)) {
        creditAndreLuizSilva.setElevation(2);
        creditJeanSilva.setElevation(2);
        creditAllanDePaula.setElevation(2);
        creditJoaoPedroRocha.setElevation(2);
        creditJoaoVitorAraujo.setElevation(2);
      }
    }

    creditJeanSilva();
    creditAllanDePaula();
    creditJoaoVitorAraujo();
  }

  private void creditJeanSilva() {
    creditJeanSilva.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent jeanSilvaTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/jeanbarrossilva"));
        startActivity(jeanSilvaTwitter);
      }
    });
  }

  private void creditAllanDePaula() {
    creditAllanDePaula.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent allanDePaulaTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/allanrigodepau2"));
        startActivity(allanDePaulaTwitter);
      }
    });
  }

  private void creditJoaoVitorAraujo() {
    creditJoaoVitorAraujo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent joaoVitorAraujoTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/joovito32974920"));
        startActivity(joaoVitorAraujoTwitter);
      }
    });
  }
}