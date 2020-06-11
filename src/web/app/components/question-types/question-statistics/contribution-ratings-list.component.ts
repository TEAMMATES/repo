import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'tm-contribution-ratings-list',
  templateUrl: './contribution-ratings-list.component.html',
  styleUrls: ['./contribution-ratings-list.component.scss'],
})
export class ContributionRatingsListComponent implements OnInit {

  @Input()
  ratingsList: number[] = [];

  constructor() { }

  ngOnInit(): void {
  }

}
