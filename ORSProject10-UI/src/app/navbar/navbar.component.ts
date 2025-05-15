import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpServiceService } from '../http-service.service';
import { ServiceLocatorService } from '../service-locator.service';
import { TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  userid: string;

  constructor(
    private translate: TranslateService,
    private route: ActivatedRoute,
    private httpService: HttpServiceService,
    private myservice: HttpClient,
    private servicelocator: ServiceLocatorService
  ) {
    const savedLang = localStorage.getItem("locale");
    translate.setDefaultLang(savedLang ? savedLang : "en");
  }

  public form = {
    error: false,
    message: null,
    data: {
      id: null,
      fname: null,
      lname: null,
      role: null,
      loginId: null
    },
    inputerror: {},
    searchParams: {},
    searchMessage: null,
    list: []
  };

  ngOnInit(): void {}

  changeLocale(locale: string): void {
    localStorage.setItem("locale", locale);
    this.translate.use(locale);
    console.log('Locale set to: ' + locale);
  }

  forward(): void {
    this.userid = localStorage.getItem("userid");
    console.log('UID---' + this.userid);
    this.servicelocator.forward("/user/" + this.userid);
  }

  isLogin(): boolean {
    const fname = localStorage.getItem('fname');
    if (fname && fname !== "null") {
      this.form.data.fname = fname;
      this.form.data.lname = localStorage.getItem("lname");
      this.form.data.loginId = localStorage.getItem("loginId");
      this.form.data.role = localStorage.getItem("role");
      return true;
    }
    return false;
  }

  goToLink(): void {
    window.open('assets/doc/index.html', '_blank');
  }

  logout(): void {
    this.httpService.get("http://localhost:8084/User/logout", (res) => {
      if (res.success) {
        localStorage.clear();
        this.form.message = res.result.message;
      }
      this.servicelocator.router.navigateByUrl('/login/true');
    });
  }
}
