import { OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { ServiceLocatorService } from './service-locator.service';
import { ActivatedRoute } from '@angular/router';
import { BaseCtl } from './base.component';

export class BaseListCtl extends BaseCtl implements OnInit {
  @ViewChildren("checkboxes") checkboxes: QueryList<ElementRef>;
  deleteRecordList: any = [];
  isMasterSel: boolean = false;
  checkList: number = 0;

  constructor(public endpoint, public locator: ServiceLocatorService, public route: ActivatedRoute) {
    super(endpoint, locator, route);
  }

  ngOnInit() {
    this.preload();
    this.search();
    this.isMasterSel = false;
  }

  display() {
    this.search();
  }

  submit() {
    this.form.pageNo = 0;
    if (this.form.searchParams.roleId === "undefined" || this.form.searchParams.id === "undefined") {
      this.form.searchParams = {};
    }
    this.search();
  }

  delete(id) {
    super.delete(id, function () {
      this.search();
    });
  }

  next() {
    this.form.pageNo++;
    this.display();
    this.isMasterSel = false;
  }

  previous() {
    if (this.form.pageNo > 0) {
      this.form.pageNo--;
      this.isMasterSel = false;
      this.searchOperation('previous');
    }
  }

  checkUncheckAll(event) {
    const checked = event.target.checked;
    this.checkboxes.forEach((element) => {
      element.nativeElement.checked = checked;
    });
  }

  checklistUpdate() {
    this.isMasterSel = false;
    this.checkList = 0;
    this.checkboxes.forEach((element) => {
      if (element.nativeElement.checked === true) {
        this.checkList += 1;
      }
    });
    if (this.checkList === this.form.list.length) {
      this.isMasterSel = true;
    }
  }

  deleteMany() {
    this.form.error = false;
    this.deleteRecordList.length = 0;
    let isRecordSelected: boolean = false;

    this.checkboxes.forEach((element) => {
      if (element.nativeElement.checked) {
        this.deleteRecordList.push(element.nativeElement.id);
        isRecordSelected = true;
      }
    });

    if (isRecordSelected) {
      super.deleteMany(this.deleteRecordList + '?pageNo=' + this.form.pageNo, function () {});
    } else {
      this.form.message = "Select Atleast One Record";
      this.form.error = true;
    }

    this.isMasterSel = false;
  }
}
