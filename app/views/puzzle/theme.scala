package views
package html.puzzle

import controllers.routes

import lila.api.Context
import lila.app.templating.Environment._
import lila.app.ui.ScalatagsTemplate._
import lila.i18n.I18nKey
import lila.puzzle.{ Puzzle, PuzzleAngle, PuzzleOpening, PuzzleTheme }

object theme {

  def list(all: PuzzleAngle.All)(implicit ctx: Context) =
    views.html.base.layout(
      title = trans.puzzle.puzzleThemes.txt(),
      moreCss = cssTag("puzzle.page")
    )(
      main(cls := "page-menu")(
        bits.pageMenu("themes"),
        div(cls := "page-menu__content box")(
          h1(trans.puzzle.puzzleThemes()),
          div(cls := "puzzle-themes")(
            all.themes take 2 map { case (cat, themes) =>
              themeCategory(cat, themes)
            },
            h2(id := "openings")("By game opening", a(href := routes.Puzzle.openings)(trans.more(), " »")),
            openingList(all.openings take 12),
            all.themes drop 2 map { case (cat, themes) =>
              themeCategory(cat, themes)
            },
            info
          )
        )
      )
    )

  def openings(openings: List[(PuzzleOpening, Int)])(implicit ctx: Context) =
    views.html.base.layout(
      title = "Puzzles by openings",
      moreCss = cssTag("puzzle.page")
    )(
      main(cls := "page-menu")(
        bits.pageMenu("openings"),
        div(cls := "page-menu__content box")(
          h1("Puzzles by openings"),
          div(cls := "puzzle-themes")(
            openingList(openings),
            info
          )
        )
      )
    )

  private def info(implicit ctx: Context) =
    p(cls := "puzzle-themes__db text", dataIcon := "")(
      trans.puzzleTheme.puzzleDownloadInformation(
        a(href := "https://database.lichess.org/")("database.lichess.org")
      )
    )

  private def themeCategory(cat: I18nKey, themes: List[PuzzleTheme.WithCount])(implicit ctx: Context) =
    frag(
      h2(id := cat.key)(cat()),
      div(cls := s"puzzle-themes__list ${cat.key.replace(":", "-")}")(
        themes.map { pt =>
          val url =
            if (pt.theme == PuzzleTheme.mix) routes.Puzzle.home
            else routes.Puzzle.show(pt.theme.key.value)
          a(cls := "puzzle-themes__link", href := (pt.count > 0).option(url.url))(
            span(
              h3(
                pt.theme.name(),
                em(pt.count.localize)
              ),
              span(pt.theme.description())
            )
          )
        },
        cat.key == "puzzle:origin" option
          a(cls := "puzzle-themes__link", href := routes.Puzzle.ofPlayer())(
            span(
              h3(trans.puzzleTheme.playerGames()),
              span(trans.puzzleTheme.playerGamesDescription())
            )
          )
      )
    )

  private def openingList(openings: List[(PuzzleOpening, Int)])(implicit ctx: Context) =
    div(cls := "puzzle-openings__list")(openings map { case (opening, count) =>
      a(cls := "puzzle-openings__link", href := routes.Puzzle.show(opening.key.value))(
        h3(
          opening.name,
          em(count.localize)
        )
      )
    })
}
