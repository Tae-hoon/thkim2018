import React, { useState, Component, Fragment } from 'react';

class Board extends Component {

    constructor(props) {
        super(props);
        this._isMounted = false;
        this.state = {
            menu1: this.props.menu1,
            menu2: this.props.menu2,
            boardType: this.props.boardType,
            boardData: {
                id: '',
                title: '',
                contents: '',
                fname: ''
            }
        }

        this.menuClick = this.menuClick.bind(this);
        this.menuClick2 = this.menuClick2.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.handleCreate2 = this.handleCreate2.bind(this);
    }

    componentDidMount(){
        console.log('===Board componentDidMount:'+this.props.boardType);
        this._ismounted = true;

        if( this.props.boardType === 'board' ){
            this.setState({
                menu1: true,
                menu2: false,
            });
        }else{
            this.setState({
                menu1: false,
                menu2: true,
            });
        }
    }

    componentWillUnmount() {
        console.log('===Board componentWillUnmount');
        this._ismounted = false;
    }

    menuClick() {
        fetch('/board')
        .then(response => response.text())
        .then(data => {            
            
            //console.log("boardType:"+data);

            this.setState({
                menu1: true,
                menu2: false,
                boardType: data,
            }, () => this.props.onCreate(this.state) );
        });
    }

    menuClick2() {
        fetch('/board2')
        .then(response => response.text())
        .then(data => {            
            
            //console.log("boardType:"+data);

            this.setState({
                menu1: false,
                menu2: true,
                boardType: data,
            }, () => this.props.onCreate(this.state) );
        });
    }

    handleCreate(data) {
        //console.log("Board handleCreate:"+data.boardData.title);

        if( this._ismounted ){
            this.setState({
                boardData: data.boardData
            });
        }
    }

    handleCreate2(data) {
        //console.log("Board handleCreate2:"+data.title);

        if( this._ismounted ){
            this.setState({
                boardData: {id: this.state.boardData.id, title: this.state.boardData.title, contents: this.state.boardData.contents, date: this.state.boardData.date, fname: data.fname}
            });
        }
    }

    render() {
        //console.log('===Board render:'+this.props.boardType);
        //console.log("menu1:"+this.state.menu1);
        //console.log("menu2:"+this.state.menu2);
        return (
            <div className="card">
                <h1>react board</h1>
                <div className="card-header">
                    <ul className="nav nav-tabs">
                    <li className="nav-item">
                        <a className={"nav-link " + (this.state.menu1 ? 'active' : '')} onClick={this.menuClick}>싱글이미지 게시판</a>
                    </li>
                    <li className="nav-item">
                        <a className={"nav-link " + (this.state.menu2 ? 'active' : '')} onClick={this.menuClick2}>멀티이미지 게시판</a>
                    </li>
                    </ul>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col-lg-4">
                            <List key={this.state.boardType} boardType={this.state.boardType} onCreate={this.handleCreate} />
                        </div>
                        <div className="col-lg-5">
                            <Detail key={this.state.boardType+this.state.boardData.id} id={this.state.boardData.id} title={this.state.boardData.title} contents={this.state.boardData.contents} date={this.state.boardData.date} fname={this.state.boardData.fname} boardType={this.state.boardType} onCreate={this.handleCreate2} />
                        </div>
                        <div className="col-lg-3">
                            <Image key={this.state.boardType+this.state.boardData.id+this.state.boardData.fname} boardType={this.state.boardType} fname={this.state.boardData.fname} />
                        </div>
                    </div>
                </div>
                <div className="card-footer">SpringBoot + MongoDB + <strong>React</strong> + bootstrap4 게시판 만들기</div>
            </div>
        );
    }
}

class List extends Component {
    constructor(props) {
        super(props);
        this._isMounted = false;
        this.state = {
            boardList: [],
            boardData: {
                id: '',
                title: '',
                contents: '',
                fname: ''
            }
        }

        this.setBoardData = this.setBoardData.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
    }

    componentDidMount(){
        console.log('===List componentDidMount');
        this._isMounted = true;

        fetch('/list')
        .then(response => response.json())
        .then(data => {            
            
            //console.log(data);
            let boardData = {};
            for(var i=0; i<data.list.length; i++){
                var contents = data.list[i].contents;
                //console.log(contents);
                //contents = contents.replace(/\n/gi,'\\n');

                if( i === 0 ){

                    boardData = {id: data.list[i].id, title: data.list[i].title, contents: contents, date: data.list[i].date, fname: data.list[i].fname};

                    this.setState({
                        boardData: boardData
                    }, () => this.setBoardData() );
                }
            }

            const boardList = data.list.map(item => (
                <ListData key={item.id} id={item.id} title={item.title} contents={item.contents} date={item.date} fname={item.fname} onCreate={this.handleCreate} />
            ));

            this.setState({
                boardList: boardList
            });
        });
    }

    componentWillUnmount() {
        //console.log('===componentWillUnmount');
        this._ismounted = false;
    }

    setBoardData(){

        //초기화시 실행
        //console.log("list data:"+this.state.boardData.title);

        this.props.onCreate(this.state);
    }

    handleCreate(data) {

        //행클릭시 실행
        //console.log("List handleCreate:"+data.boardData.title);

        if( this._isMounted ){
            this.setState({
                boardData: data.boardData
            }, () => this.props.onCreate(this.state) );
        }
    }

    render() {
        const divStyle = {
            minHeight: '500px',
            maxHeight: '500px',
            overflowY: 'auto'
        };

        return (
            <div className="card" style={divStyle}>
                <table className="table">
                    <thead>
                        <tr>
                        <th>게시물 리스트</th>
                        </tr>
                    </thead>
                    <tbody>
                        {/* <tr>
                            <td>게시물 1</td>
                        </tr>
                        <tr>
                            <td>게시물 2</td>
                        </tr> */}
                        {this.state.boardList}
                    </tbody>
                </table>
            </div>
        );
    }
}

class ListData extends Component {  

    constructor(props) {
        super(props);
        this._ismounted = false;
        this.state = {
            boardData: {
                id: '',
                title: '',
                contents: '',
                fname: ''
            }
        }

        this.setDetail = this.setDetail.bind(this);
    }

    componentDidMount(){
        //console.log('===ListData componentDidMount:'+this.props.id);
        this._ismounted = true;

        /* this.setState({
            boardData: {id: this.props.id, title: this.props.title, contents: this.props.contents, date: this.props.date, fname: this.props.fname}
        }, () => this.props.onCreate(this.state) ); */
    }

    componentWillUnmount() {
        //console.log('===componentWillUnmount');
        this._ismounted = false;
    }

    setDetail(){
        //console.log("title:"+this.props.title);

        if( this._ismounted ){
            this.setState({
                boardData: {id: this.props.id, title: this.props.title, contents: this.props.contents, date: this.props.date, fname: this.props.fname}
            }, () => this.props.onCreate(this.state) );
        }
    }

    render() {
        const lineStyle = {
            float: 'right',
        };

        return (
            <tr onClick={this.setDetail}>
                <td>{this.props.title}<span style={lineStyle}>{this.props.date}</span></td>
            </tr>            
        );
    }
}

class Detail extends Component {
    constructor(props) {
        super(props);
        this._ismounted = false;
        this.state = {
            id: '',
            title: '',
            contents: '',
            fname: '',
            //selectedFiles: null
        }

        this.idRef = React.createRef();
        this.titleRef = React.createRef();
        this.contentsRef = React.createRef();
        this.fileRef = React.createRef();

        this.save = this.save.bind(this);
        this.cancel = this.cancel.bind(this);
        this.del = this.del.bind(this);
        this.delimg = this.delimg.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.fileHandler = this.fileHandler.bind(this);
    }

    componentDidMount(){
        console.log('===Detail componentDidMount');
        this._ismounted = true;

        if( this.props.title !== undefined && this.props.title !== '' ){
            //console.log("detail title:"+this.props.title);
            
            if( this._ismounted ){
                this.setState({
                    id: this.props.id,
                    title: this.props.title,
                    contents: this.props.contents,
                    fname: this.props.fname
                });
            }
        }
    }

    componentWillUnmount() {
        //console.log('===componentWillUnmount');
        this._ismounted = false;
    }

    save(e) {
        if( this.props.title !== undefined && this.props.title !== '' ){
            //console.log("==save==:"+this.props.title);

            //console.log("boardType:"+this.props.boardType);
            //console.log("id:"+this.idRef.current.value);
            //console.log("title:"+this.titleRef.current.value);
            //console.log("file1:"+this.fileRef.current.files[0].name);
            //console.log("file2:"+this.fileRef.current.files.length);
            //console.log("file3:"+this.state.selectedFiles);

            if( !window.confirm("저장하시겠습니까?") ){
                return;
            }
            
            let formData = new FormData();
            formData.append('id', this.idRef.current.value);
            formData.append('title', this.titleRef.current.value);
            formData.append('contents', this.contentsRef.current.value);
            //formData.append('file', this.state.selectedFiles);

            if( this.props.boardType === 'board' ){
                formData.append('file', this.fileRef.current.files[0]);
            }else if( this.props.boardType === 'board2' ){
                if( this.fileRef.current.files.length > 0 ){
                    for(let i=0; i<this.fileRef.current.files.length; i++){
                        formData.append('file', this.fileRef.current.files[i]);
                    }
                }else{
                    formData.append('file', this.fileRef.current.files[0]);
                }
            }
            
            let url = "/add";
            if( this.props.boardType === 'board' ){
                if( this.idRef.current.value !== '' ){                
                    url = "/mod";
                }
            }else if( this.props.boardType === 'board2' ){
                url = "/add2";
                if( this.idRef.current.value !== '' ){                
                    url = "/mod2";
                }
            }

            fetch(url, {
                method: 'POST',
                headers: {
                    //'Content-Type': 'application/json',
                    //"content-type": "multipart/form-data"
                },
                body: formData
              })
            .then(response => response.json())
            .then(data => {            
                
                if(data.returnCode === 'success'){
                    window.location.reload();
                }else{
                    window.alert(data.returnDesc);
                }
            });
        }
    }

    cancel(e) {
        if( this.props.title !== undefined && this.props.title !== '' ){
            //console.log("==cancel==:"+this.props.title);

            this.setState({
                id: '',
                title: '',
                contents: '',
                fname: ''
            }, () => this.props.onCreate(this.state) );
        }
    }

    del(e) {
        if( this.props.title !== undefined && this.props.title !== '' ){
            //console.log("==del==:"+this.props.title);

            if( this.idRef.current.value === '' ){
                window.alert("삭제할 데이터가 없습니다.");
                return;
            }
            if( !window.confirm("삭제하시겠습니까?") ){
                return;
            }
            
            let formData = new FormData();
            formData.append('id', this.idRef.current.value);
            formData.append('title', this.titleRef.current.value);
            formData.append('contents', this.contentsRef.current.value);
            
            fetch('/del', {
                method: 'POST',
                headers: {
                    //'Content-Type': 'application/json',
                },
                body: formData
              })
            .then(response => response.json())
            .then(data => {            
                
                if(data.returnCode === 'success'){
                    window.location.reload();
                }else{
                    window.alert(data.returnDesc);
                }
            });
        }
    }

    delimg(e) {

        if( this.props.title !== undefined && this.props.title !== '' ){
            if( this.idRef.current.value === '' ){
                window.alert("삭제할 데이터가 없습니다.");
                return;
            }
            if( !window.confirm("그림을 삭제하시겠습니까?") ){
                return;
            }
            
            let formData = new FormData();
            formData.append('id', this.idRef.current.value);
            formData.append('title', this.titleRef.current.value);
            formData.append('contents', this.contentsRef.current.value);
            
            fetch('/delimg', {
                method: 'POST',
                headers: {
                    //'Content-Type': 'application/json',
                },
                body: formData
              })
            .then(response => response.json())
            .then(data => {            
                
                if(data.returnCode === 'success'){
                    window.location.reload();
                }else{
                    window.alert(data.returnDesc);
                }
            });
        }
    }

    handleChange(){
        if( this._ismounted ){
            this.setState({
                id: this.idRef.current.value,
                title: this.titleRef.current.value,
                contents: this.contentsRef.current.value,
            });
        }
    }

    fileHandler = (e) => {
        /* console.log("file selected");
        const files = e.target.files;
          this.setState({
            selectedFiles: files
        }) */
    };

    render() {

        const divStyle = {
            minHeight: '500px',
            maxHeight: '1000px'
        };
        const divCenter = {
            textAlign: 'center',
        };

        return (
            <div className="card bg-light text-dark" style={divStyle}>
                <form name="form1" action="">
                    <div className="form-group">
                    <label className="control-label">제목:</label>
                    <div>
                        <input type="text" ref={this.titleRef} className="form-control" placeholder="제목을 입력하세요" onChange={this.handleChange} value={this.state.title} />
                    </div>
                    </div>
                    <div className="form-group">
                    <label className="control-label">내용:</label>
                    <div> 
                        <textarea className="form-control" ref={this.contentsRef} rows="10" onChange={this.handleChange} value={this.state.contents}></textarea>
                    </div>
                    </div>
                    <div className="form-group">
                        <label className="control-label">이미지첨부: jpg,gif,png</label>
                        <div>
                            {
                            (this.props.boardType === 'board')
                            ? <input type="file" ref={this.fileRef} className="form-control" onChange={this.fileHandler} name="file" />
                            : <input type="file" ref={this.fileRef} className="form-control" multiple onChange={this.fileHandler} name="file" />
                            }
                        </div>
                    </div>
                    <input type="hidden" ref={this.idRef} name="id" value={this.state.id} />
                </form>
                <div style={divCenter}>
                    <div className="btn-group">
                        <button type="button" className="btn btn-primary" onClick={this.save}>저장</button>
                        <button type="button" className="btn btn-secondary" onClick={this.cancel}>취소</button>
                        <button type="button" className="btn btn-danger" onClick={this.del}>삭제</button>
                        <button type="button" className="btn btn-info" onClick={this.delimg}>그림삭제</button>
                    </div>
                </div>
            </div>
        );
    }
}

class Image extends Component {
    constructor(props) {
        super(props);
        this._ismounted = false;
        this.state = {
            image: '',
            imageList: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.createMarkup = this.createMarkup.bind(this);
    }

    componentDidMount(){
        console.log('===Image componentDidMount:'+this.props.boardType);
        this._ismounted = true;

        if( this._ismounted ){
            if( this.props.boardType === 'board' ){

                if( this.props.fname !== '' ){
                    fetch('/img?fname='+encodeURIComponent(this.props.fname))
                    .then(response => response.text())
                    .then(data => {            
                        
                        //console.log("image:"+data);

                        this.setState({
                            image: "data:image/jpeg;base64,"+data
                        });
                    });
                }

            }else if( this.props.boardType === 'board2' ){

                if( this.props.fname !== '' ){
                    fetch('/img2?fname='+encodeURIComponent(this.props.fname))
                    .then(response => response.text())
                    .then(data => {            
                        
                        //console.log("image:"+data);

                        this.setState({
                            imageList: data
                        });
                    });
                }
            }
        }
    }

    componentWillUnmount() {
        //console.log('===componentWillUnmount');
        this._ismounted = false;
    }

    handleChange(){

    }

    createMarkup() {
        return {__html: this.state.imageList};
    }

    render() {
        const divStyle1 = {
            minHeight: '500px',
            maxHeight: '500px',
            overflowY: 'auto',
            display: 'block'
        };
        const divStyle1_1 = {
            minHeight: '500px',
            maxHeight: '1000px',
            display: 'none'
        };
        const divStyle2 = {
            minHeight: '500px',
            maxHeight: '500px',
            overflowY: 'auto',
            display: 'block'
        };
        const divStyle2_1 = {
            minHeight: '500px',
            maxHeight: '1000px',
            display: 'none'
        };
        const imgStyle = {
            width: '100%',
        };

        return (
            <Fragment>
                <div className="card bg-light text-dark" style={(this.props.boardType === 'board') ? divStyle1 : divStyle1_1}>
                    <img style={imgStyle} alt="image" onChange={this.handleChange} src={this.state.image}></img>
                </div>
                <div className="card bg-light text-dark" style={(this.props.boardType === 'board2') ? divStyle2 : divStyle2_1}>
                    <span dangerouslySetInnerHTML={this.createMarkup()}></span>
                </div>
            </Fragment>
        );
    }
}



export default Board;